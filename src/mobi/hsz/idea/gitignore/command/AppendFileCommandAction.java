/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.command;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Command action that appends specified file to rules list.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class AppendFileCommandAction extends WriteCommandAction<PsiFile> {
    /** Current project. */
    private final Project project;

    /** Current file. */
    private final PsiFile file;

    /** Rules set to add. */
    private final Set<String> content;

    /**
     * Builds a new instance of {@link AppendFileCommandAction}.
     * Takes a {@link Set} of the rules to add.
     *
     * @param project current project
     * @param file    working file
     * @param content rules set
     */
    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull Set<String> content) {
        super(project, file);
        this.project = project;
        this.file = file;
        this.content = content;
    }

    /**
     * Builds a new instance of {@link AppendFileCommandAction}.
     * Takes a {@link String} rule.
     *
     * @param project current project
     * @param file    working file
     * @param content rule
     */
    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull final String content) {
        super(project, file);
        this.project = project;
        this.file = file;
        this.content = ContainerUtil.newHashSet();
        if (!content.isEmpty()) {
            this.content.add(content);
        }
    }

    /**
     * Adds {@link #content} to the given {@link #file}. Checks if file contains content and sends a notification.
     *
     * @param result ignored parameter
     * @throws Throwable
     */
    @Override
    protected void run(@NotNull Result<PsiFile> result) throws Throwable {
        if (content.isEmpty()) {
            return;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            for (PsiElement element : file.getChildren()) {
                if (content.contains(element.getText())) {
                    Notifications.Bus.notify(new Notification(IgnoreLanguage.GROUP,
                            IgnoreBundle.message("action.appendFile.entryExists", element.getText()),
                            IgnoreBundle.message("action.appendFile.entryExists.in", Utils.getRelativePath(project.getBaseDir(), file.getVirtualFile())),
                            NotificationType.WARNING), project);
                    content.remove(element.getText());
                }
            }
            for (String entry : content) {
                document.insertString(document.getTextLength(), "\n" + StringUtil.replace(entry, "\r", ""));
            }
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
