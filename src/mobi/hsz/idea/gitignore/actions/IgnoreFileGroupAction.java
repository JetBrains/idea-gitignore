/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.ExternalFileException;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Group action that ignores specified file or directory.
 * {@link ActionGroup} expands single action into a more child options to allow user specify the {@link mobi.hsz.idea.gitignore.psi.GitignoreFile}
 * that will be used for file's path storage.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreFileGroupAction extends ActionGroup {
    /** List of suitable Gitignore {@link VirtualFile}s that can be presented in an IgnoreFile action. */
    private final List<VirtualFile> files = new ArrayList<VirtualFile>();

    /** {@link Project}'s base directory. */
    private VirtualFile baseDir;

    /**
     * Builds a new instance of {@link IgnoreFileGroupAction}.
     * Describes action's presentation.
     */
    public IgnoreFileGroupAction() {
        Presentation p = getTemplatePresentation();
        p.setText(GitignoreBundle.message("action.addToGitignore"));
        p.setDescription(GitignoreBundle.message("action.addToGitignore.description"));
        p.setIcon(Icons.FILE);
    }

    /**
     * Presents a list of suitable Gitignore files that can cover currently selected {@link VirtualFile}.
     * Shows a subgroup with available files or one option if only one Gitignore file is available.
     *
     * @param e action event
     */
    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = e.getData(CommonDataKeys.PROJECT);
        files.clear();
        if (project != null && file != null) {
            try {
                files.addAll(Utils.getSuitableGitignoreFiles(project, file));
                e.getPresentation().setVisible(true);
                Collections.reverse(files);
                baseDir = project.getBaseDir();
            } catch (ExternalFileException e1) {
                e.getPresentation().setVisible(false);
            }
        }
        setPopup(files.size() > 1);
    }

    /**
     * Creates subactions bound to the specified Gitignore {@link VirtualFile}s using {@link IgnoreFileAction}.
     *
     * @param e action event
     * @return actions list
     */
    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        AnAction[] actions;
        int size = files.size();
        if (size == 0) {
            actions = new AnAction[]{ new IgnoreFileAction() };
        } else {
            actions = new AnAction[size];
            for (int i = 0; i < files.size(); i++) {
                VirtualFile file = files.get(i);
                IgnoreFileAction action = new IgnoreFileAction(file);
                actions[i] = action;

                if (size > 1) {
                    String name = Utils.getRelativePath(baseDir, file);
                    action.getTemplatePresentation().setText(name);
                }
            }
        }
        return actions;
    }
}
