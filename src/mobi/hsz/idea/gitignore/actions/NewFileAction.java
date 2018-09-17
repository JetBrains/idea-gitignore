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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.IdeView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Creates new file or returns existing one.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @since 0.1
 */
@SuppressWarnings("ComponentNotRegistered")
public class NewFileAction extends AnAction implements DumbAware {
    /** Current file type. */
    private final IgnoreFileType fileType;

    /** Builds a new instance of {@link NewFileAction}. */
    public NewFileAction(@NotNull IgnoreFileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Creates new Gitignore file if it does not exist or uses an existing one and opens {@link GeneratorDialog}.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final IdeView view = e.getRequiredData(LangDataKeys.IDE_VIEW);

        VirtualFile fixedDirectory = fileType.getIgnoreLanguage().getFixedDirectory(project);
        PsiDirectory directory;

        if (fixedDirectory != null) {
            directory = PsiManager.getInstance(project).findDirectory(fixedDirectory);
        } else {
            directory = view.getOrChooseDirectory();
        }

        if (directory == null) {
            return;
        }

        GeneratorDialog dialog;
        String filename = fileType.getIgnoreLanguage().getFilename();
        PsiFile file = directory.findFile(filename);
        VirtualFile virtualFile = file == null ? directory.getVirtualFile().findChild(filename) : file.getVirtualFile();

        if (file == null && virtualFile == null) {
            CreateFileCommandAction action = new CreateFileCommandAction(project, directory, fileType);
            dialog = new GeneratorDialog(project, action);
        } else {
            Notifications.Bus.notify(new Notification(
                    fileType.getLanguageName(),
                    IgnoreBundle.message("action.newFile.exists", fileType.getLanguageName()),
                    IgnoreBundle.message("action.newFile.exists.in", virtualFile.getPath()),
                    NotificationType.INFORMATION
            ), project);

            if (file == null) {
                file = Utils.getPsiFile(project, virtualFile);
            }

            dialog = new GeneratorDialog(project, file);
        }

        dialog.show();
        file = dialog.getFile();

        if (file != null) {
            Utils.openFile(project, file);
        }
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     *
     * @param e action event
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final IdeView view = e.getData(LangDataKeys.IDE_VIEW);

        final PsiDirectory[] directory = view != null ? view.getDirectories() : null;
        if (directory == null || directory.length == 0 || project == null ||
                !this.fileType.getIgnoreLanguage().isNewAllowed()) {
            e.getPresentation().setVisible(false);
        }
    }
}
