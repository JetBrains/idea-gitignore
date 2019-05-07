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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.Notify;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.util.Set;

import static mobi.hsz.idea.gitignore.IgnoreBundle.BUNDLE_NAME;

/**
 * Action that adds currently selected {@link VirtualFile} to the specified Ignore {@link VirtualFile}.
 * Action is added to the IDE context menus not directly but with {@link IgnoreFileGroupAction} action.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
@SuppressWarnings("ComponentNotRegistered")
public class IgnoreFileAction extends DumbAwareAction {
    /**
     * Ignore {@link VirtualFile} that will be used for current action.
     */
    private final VirtualFile ignoreFile;

    /**
     * Current ignore file type.
     */
    private final IgnoreFileType fileType;

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Default project's Ignore file will be used.
     */
    public IgnoreFileAction() {
        this(null);
    }

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Describes action's presentation.
     *
     * @param virtualFile Gitignore file
     */
    public IgnoreFileAction(@Nullable VirtualFile virtualFile) {
        this(Utils.getFileType(virtualFile), virtualFile);
    }

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Describes action's presentation.
     *
     * @param fileType    Current file type
     * @param virtualFile Gitignore file
     */
    public IgnoreFileAction(@Nullable IgnoreFileType fileType, @Nullable VirtualFile virtualFile) {
        this(fileType, virtualFile, "action.addToIgnore", "action.addToIgnore.description");
    }

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Describes action's presentation.
     *
     * @param fileType       Current file type
     * @param virtualFile    Gitignore file
     * @param textKey        Action presentation's text key
     * @param descriptionKey Action presentation's description key
     */
    public IgnoreFileAction(@Nullable IgnoreFileType fileType, @Nullable VirtualFile virtualFile,
                            @PropertyKey(resourceBundle = BUNDLE_NAME) String textKey,
                            @PropertyKey(resourceBundle = BUNDLE_NAME) String descriptionKey) {
        super(IgnoreBundle.message(textKey, fileType != null ? fileType.getIgnoreLanguage().getFilename() : null),
                IgnoreBundle.message(
                        descriptionKey,
                        fileType != null ? fileType.getIgnoreLanguage().getFilename() : null
                ),
                fileType != null ? fileType.getIcon() : null);
        this.ignoreFile = virtualFile;
        this.fileType = fileType;
    }

    /**
     * Adds currently selected {@link VirtualFile} to the {@link #ignoreFile}.
     * If {@link #ignoreFile} is null, default project's Gitignore file will be used.
     * Files that cannot be covered with Gitignore file produces error notification.
     * When action is performed, Gitignore file is opened with additional content added
     * using {@link AppendFileCommandAction}.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final VirtualFile[] files = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiFile ignore = null;
        if (ignoreFile != null) {
            ignore = Utils.getPsiFile(project, ignoreFile);
        }
        if (ignore == null && fileType != null) {
            ignore = Utils.getIgnoreFile(project, fileType, null, true);
        }

        if (ignore != null) {
            Set<String> paths = ContainerUtil.newHashSet();
            for (VirtualFile file : files) {
                final String path = getPath(ignore.getVirtualFile().getParent(), file);
                if (path.isEmpty()) {
                    final VirtualFile baseDir = Utils.getModuleRootForFile(file, project);
                    if (baseDir != null) {
                        Notify.show(
                                project,
                                IgnoreBundle.message(
                                        "action.ignoreFile.addError",
                                        Utils.getRelativePath(baseDir, file)
                                ),
                                IgnoreBundle.message(
                                        "action.ignoreFile.addError.to",
                                        Utils.getRelativePath(baseDir, ignore.getVirtualFile())
                                ),
                                NotificationType.ERROR
                        );
                    }
                } else {
                    paths.add(path);
                }
            }
            Utils.openFile(project, ignore);
            try {
                new AppendFileCommandAction(project, ignore, paths, false, false).execute();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Shows action in the context menu if current file is covered by the specified {@link #ignoreFile}.
     *
     * @param e action event
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        final VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        final Project project = e.getProject();

        if (project == null || files == null) {
            e.getPresentation().setVisible(false);
        }
    }

    /**
     * Gets the file's path relative to the specified root directory.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    @NotNull
    protected String getPath(@NotNull VirtualFile root, @NotNull VirtualFile file) {
        String path = StringUtil.notNullize(Utils.getRelativePath(root, file));
        path = StringUtil.escapeChar(path, '[');
        path = StringUtil.escapeChar(path, ']');
        path = StringUtil.trimLeading(path, '/');
        return path.isEmpty() ? path : '/' + path;
    }
}
