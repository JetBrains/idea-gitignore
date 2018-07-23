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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.ui.untrackFiles.UntrackFilesDialog;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentMap;

/**
 * Action that invokes {@link UntrackFilesDialog} dialog.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7.2
 */
public class HandleTrackedIgnoredFilesAction extends AnAction {
    /** Builds a new instance of {@link HandleTrackedIgnoredFilesAction}. */
    public HandleTrackedIgnoredFilesAction() {
        super(IgnoreBundle.message(
                "action.handleTrackedIgnoredFiles"),
                IgnoreBundle.message("action.handleTrackedIgnoredFiles.description"),
                Icons.IGNORE
        );
    }

    /**
     * Toggles {@link mobi.hsz.idea.gitignore.settings.IgnoreSettings#hideIgnoredFiles} value.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        new UntrackFilesDialog(project, getTrackedIgnoredFiles(e)).show();
    }

    /**
     * Shows action in the context menu.
     *
     * @param e action event
     */
    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(!getTrackedIgnoredFiles(e).isEmpty());
    }

    /**
     * Helper method to return tracked and ignored files map.
     *
     * @param event current event
     * @return map of files
     */
    private ConcurrentMap<VirtualFile, VcsRoot> getTrackedIgnoredFiles(@NotNull AnActionEvent event) {
        final Project project = event.getProject();

        if (project != null) {
            return IgnoreManager.getInstance(project).getConfirmedIgnoredFiles();
        }

        return ContainerUtil.createConcurrentWeakMap();
    }
}
