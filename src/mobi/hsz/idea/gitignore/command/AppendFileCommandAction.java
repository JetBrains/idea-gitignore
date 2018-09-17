/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Constants;
import mobi.hsz.idea.gitignore.util.Notify;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Command action that appends specified file to rules list.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class AppendFileCommandAction extends CommandAction<PsiFile> {
    /** Current project. */
    private final Project project;

    /** Current file. */
    private final PsiFile file;

    /** Rules set to add. */
    private final Set<String> content;

    /** {@link PsiDocumentManager} instance. */
    private final PsiDocumentManager manager;

    /** Ignore duplicated entries. */
    private final boolean ignoreDuplicates;

    /** Ignore comments and empty lines. */
    private final boolean ignoreComments;

    /** Defines if new content should be inserted at the cursor's position or at the document end. */
    private final boolean insertAtCursor;

    /**
     * Builds a new instance of {@link AppendFileCommandAction}.
     * Takes a {@link Set} of the rules to add.
     *
     * @param project          current project
     * @param file             working file
     * @param content          rule
     * @param ignoreDuplicates ignore duplicated entries
     * @param ignoreComments   ignore comments and empty lines
     */
    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull Set<String> content,
                                   boolean ignoreDuplicates, boolean ignoreComments) {
        super(project);
        this.project = project;
        this.file = file;
        this.content = content;
        this.manager = PsiDocumentManager.getInstance(project);
        this.ignoreDuplicates = ignoreDuplicates;
        this.ignoreComments = ignoreComments;
        this.insertAtCursor = IgnoreSettings.getInstance().isInsertAtCursor();
    }

    /**
     * Builds a new instance of {@link AppendFileCommandAction}.
     * Takes a {@link String} rule.
     *
     * @param project          current project
     * @param file             working file
     * @param content          rule
     * @param ignoreDuplicates ignore duplicated entries
     * @param ignoreComments   ignore comments and empty lines
     */
    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull final String content,
                                   boolean ignoreDuplicates, boolean ignoreComments) {
        this(project, file, ContainerUtil.newHashSet(content), ignoreDuplicates, ignoreComments);
    }

    /**
     * Adds {@link #content} to the given {@link #file}. Checks if file contains content and sends a notification.
     *
     * @return previously provided file
     */
    @Override
    protected PsiFile compute() {
        if (!content.isEmpty()) {
            final Document document = manager.getDocument(file);
            if (document != null) {
                file.acceptChildren(new IgnoreVisitor() {
                    @Override
                    public void visitEntry(@NotNull IgnoreEntry entry) {
                        final VirtualFile baseDir = project.getBaseDir();
                        if (content.contains(entry.getText()) && baseDir != null) {
                            Notify.show(
                                    project,
                                    IgnoreBundle.message("action.appendFile.entryExists", entry.getText()),
                                    IgnoreBundle.message(
                                            "action.appendFile.entryExists.in",
                                            Utils.getRelativePath(baseDir, file.getVirtualFile())
                                    ),
                                    NotificationType.WARNING
                            );
                            content.remove(entry.getText());
                        }
                    }
                });

                int offset = document.getTextLength();

                if (insertAtCursor) {
                    Editor[] editors = EditorFactory.getInstance().getEditors(document);
                    if (editors.length > 0) {
                        VisualPosition position = editors[0].getSelectionModel().getSelectionStartPosition();
                        if (position != null) {
                            offset = document.getLineStartOffset(position.line);
                        }
                    }
                }

                for (String entry : content) {
                    if (ignoreDuplicates) {
                        List<String> currentLines = ContainerUtil.filter(
                                document.getText().split(Constants.NEWLINE),
                                s -> !s.isEmpty() && !s.startsWith(Constants.HASH)
                        );

                        List<String> entryLines = new ArrayList<>(Arrays.asList(entry.split(Constants.NEWLINE)));
                        Iterator<String> iterator = entryLines.iterator();
                        while (iterator.hasNext()) {
                            String line = iterator.next().trim();
                            if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                                continue;
                            }

                            if (currentLines.contains(line)) {
                                iterator.remove();
                            } else {
                                currentLines.add(line);
                            }
                        }

                        entry = StringUtil.join(entryLines, Constants.NEWLINE);
                    }

                    if (ignoreComments) {
                        List<String> entryLines = new ArrayList<>(Arrays.asList(entry.split(Constants.NEWLINE)));
                        Iterator<String> iterator = entryLines.iterator();
                        while (iterator.hasNext()) {
                            String line = iterator.next().trim();
                            if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                                iterator.remove();
                            }
                        }

                        entry = StringUtil.join(entryLines, Constants.NEWLINE);
                    }

                    entry = StringUtil.replace(entry, "\r", "");
                    if (!StringUtil.isEmpty(entry)) {
                        entry += Constants.NEWLINE;
                    }
                    if (!insertAtCursor && !document.getText().endsWith(Constants.NEWLINE)
                            && !StringUtil.isEmpty(entry)) {
                        entry = Constants.NEWLINE + entry;
                    }

                    document.insertString(offset, entry);
                    offset += entry.length();
                }

                manager.commitDocument(document);
            }
        }
        return file;
    }
}
