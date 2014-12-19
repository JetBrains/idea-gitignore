/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.GitignoreFileType;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.ui.IgnoredTreeDialog;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import org.jetbrains.annotations.NotNull;

/**
 * Shows dialog with project tree and marks files covered by the current ignore file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class ShowIgnoredAction extends DumbAwareAction {
    /** Current file type. */
    private final IgnoreFileType fileType;

    /**
     * Builds a new instance of {@link ShowIgnoredAction}.
     * Describes action's presentation.
     */
    protected ShowIgnoredAction() {
        this(GitignoreFileType.INSTANCE);
    }

    /**
     * Builds a new instance of {@link ShowIgnoredAction}.
     * Describes action's presentation.
     *
     * @param fileType Current file type
     */
    protected ShowIgnoredAction(@NotNull IgnoreFileType fileType) {
        super(IgnoreBundle.message("action.showIgnored"), IgnoreBundle.message("action.showIgnored.description"), fileType.getIcon());
        this.fileType = fileType;
    }

    /**
     * Shows {@link IgnoredTreeDialog} for the given ignore file.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        if (file == null || !file.getLanguage().equals(fileType.getIgnoreLanguage())) {
            return;
        }

        new IgnoredTreeDialog(file).showDialog();
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     *
     * @param e action event
     */
    @Override
    public void update(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || file == null || !file.getName().equals(fileType.getIgnoreLanguage().getFilename())) {
            e.getPresentation().setVisible(false);
        }
    }
}
