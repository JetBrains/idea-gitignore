/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.ui.untrackFiles;

import com.intellij.dvcs.repo.Repository;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link FileTreeNode} is an implementation of checkbox tree node.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7
 */
public class FileTreeNode extends CheckedTreeNode {
    /** Current {@link VirtualFile} element. */
    @NotNull
    private final VirtualFile file;

    /** Current {@link Project} element. */
    @NotNull
    private final Project project;
    
    /** {@link Repository} of the given {@link #file}. */
    @Nullable
    private final Repository repository;

    /**
     * Creates a new instance of {@link FileTreeNode}.
     *
     * @param file current file to render
     */
    public FileTreeNode(@NotNull Project project, @NotNull VirtualFile file, @Nullable Repository repository) {
        super(file);
        this.project = project;
        this.file = file;
        this.repository = repository;
    }

    /**
     * Returns current project.
     *
     * @return project
     */
    @NotNull
    public Project getProject() {
        return project;
    }

    /**
     * Returns current file.
     *
     * @return file
     */
    @NotNull
    public VirtualFile getFile() {
        return file;
    }

    /**
     * Returns {@link Repository} for given {@link #file}.
     * 
     * @return repository
     */
    @Nullable
    public Repository getRepository() {
        return repository;
    }
}
