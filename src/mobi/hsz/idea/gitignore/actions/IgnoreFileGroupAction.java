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

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.ExternalFileException;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Group action that ignores specified file or directory.
 * {@link ActionGroup} expands single action into a more child options to allow user specify the {@link mobi.hsz.idea.gitignore.psi.IgnoreFile}
 * that will be used for file's path storage.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreFileGroupAction extends ActionGroup {
    /** List of suitable Gitignore {@link VirtualFile}s that can be presented in an IgnoreFile action. */
    private final Map<IgnoreFileType, List<VirtualFile>> files = ContainerUtil.newHashMap();

    /** {@link Project}'s base directory. */
    private VirtualFile baseDir;

    /**
     * Builds a new instance of {@link IgnoreFileGroupAction}.
     * Describes action's presentation.
     */
    public IgnoreFileGroupAction() {
        Presentation p = getTemplatePresentation();
        p.setText(IgnoreBundle.message("action.addToIgnore.group"));
        p.setDescription(IgnoreBundle.message("action.addToIgnore.group.description"));
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
        final Presentation presentation = e.getPresentation();
        files.clear();

        if (project != null && file != null) {
            try {
                presentation.setVisible(true);
                baseDir = project.getBaseDir();

                for (IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
                    final IgnoreFileType fileType = language.getFileType();
                    List<VirtualFile> list = Utils.getSuitableIgnoreFiles(project, fileType, file);
                    Collections.reverse(list);
                    files.put(fileType, list);
                }
            } catch (ExternalFileException e1) {
                presentation.setVisible(false);
            }
        }

        setPopup(countFiles() > 1);
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
        int count = countFiles();

        if (count == 0) {
            actions = new AnAction[0];
        } else {
            actions = new AnAction[count];

            int i = 0;
            for(Map.Entry<IgnoreFileType, List<VirtualFile>> entry : files.entrySet()) {
                for (VirtualFile file : entry.getValue()) {
                    IgnoreFileAction action = new IgnoreFileAction(file);
                    actions[i++] = action;

                    String name = Utils.getRelativePath(baseDir, file);
                    if (count == 1) {
                        name = IgnoreBundle.message("action.addToIgnore.group.noPopup", name);
                    }

                    Presentation presentation = action.getTemplatePresentation();
                    presentation.setIcon(entry.getKey().getIcon());
                    presentation.setText(name);
                }
            }
        }
        return actions;
    }

    /**
     * Counts items in {@link #files} map.
     *
     * @return files amount
     */
    private int countFiles() {
        int size = 0;
        for (List value : files.values()) {
            size += value.size();
        }
        return size;
    }
}
