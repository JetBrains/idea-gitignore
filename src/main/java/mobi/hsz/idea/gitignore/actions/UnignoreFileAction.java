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

import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action that adds currently selected {@link VirtualFile} to the specified Ignore {@link VirtualFile} as unignored.
 * Action is added to the IDE context menus not directly but with {@link UnignoreFileGroupAction} action.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.6
 */
@SuppressWarnings("ComponentNotRegistered")
public class UnignoreFileAction extends IgnoreFileAction {
    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Default project's Ignore file will be used.
     */
    public UnignoreFileAction() {
        this(null);
    }

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Describes action's presentation.
     *
     * @param virtualFile Gitignore file
     */
    public UnignoreFileAction(@Nullable VirtualFile virtualFile) {
        this(Utils.getFileType(virtualFile), virtualFile);
    }

    /**
     * Builds a new instance of {@link IgnoreFileAction}.
     * Describes action's presentation.
     *
     * @param fileType    Current file type
     * @param virtualFile Gitignore file
     */
    public UnignoreFileAction(@Nullable IgnoreFileType fileType, @Nullable VirtualFile virtualFile) {
        super(fileType, virtualFile, "action.addToUnignore", "action.addToUnignore.description");
    }

    /**
     * Gets the file's path relative to the specified root directory.
     * Returns string with negation.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    @NotNull
    @Override
    protected String getPath(@NotNull VirtualFile root, @NotNull VirtualFile file) {
        final String path = super.getPath(root, file);
        return path.isEmpty() ? path : '!' + path;
    }
}
