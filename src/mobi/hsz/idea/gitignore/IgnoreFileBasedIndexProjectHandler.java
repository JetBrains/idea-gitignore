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

package mobi.hsz.idea.gitignore;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.IndexableFileSet;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor;
import org.jetbrains.annotations.NotNull;

import static mobi.hsz.idea.gitignore.IgnoreManager.RefreshStatusesListener.REFRESH_STATUSES;

/**
 * Project component that registers {@link IndexableFileSet} that counts into indexing files located outside of the
 * project.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public class IgnoreFileBasedIndexProjectHandler implements IndexableFileSet, ProjectComponent {
    /** Current project. */
    private final Project project;

    /** {@link ProjectManager} instance. */
    @NotNull
    private final ProjectManager projectManager;

    /** {@link FileBasedIndex} instance. */
    @NotNull
    private final FileBasedIndex index;

    /** Project listener to remove {@link IndexableFileSet} from the indexable sets. */
    @NotNull
    private final ProjectManagerListener projectListener = new ProjectManagerListener() {
        public void projectClosing(@NotNull Project project) {
            index.removeIndexableSet(IgnoreFileBasedIndexProjectHandler.this);
        }
    };

    /**
     * Constructor.
     *
     * @param project        current project
     * @param projectManager project manager instance
     * @param index          index instance
     */
    public IgnoreFileBasedIndexProjectHandler(@NotNull final Project project, @NotNull ProjectManager projectManager,
                                              @NotNull final FileBasedIndex index) {
        this.project = project;
        this.projectManager = projectManager;
        this.index = index;

        StartupManager.getInstance(project).registerPreStartupActivity(() -> {
            index.registerIndexableSet(IgnoreFileBasedIndexProjectHandler.this, project);
            project.getMessageBus().syncPublisher(REFRESH_STATUSES).refresh();
        });
    }

    /** Initialize component and add {@link #projectListener}. */
    public void initComponent() {
        projectManager.addProjectManagerListener(project, projectListener);
    }

    /** Dispose component and remove {@link #projectListener}. */
    public void disposeComponent() {
        projectManager.removeProjectManagerListener(project, projectListener);
    }

    /**
     * Checks if given file is in {@link ExternalIndexableSetContributor} set.
     *
     * @param file to check
     * @return is in set
     */
    @Override
    public boolean isInSet(@NotNull VirtualFile file) {
        return file.getFileType() instanceof IgnoreFileType &&
                ExternalIndexableSetContributor.getAdditionalFiles(project).contains(file);
    }

    /**
     * Iterates over given file's children.
     *
     * @param file     to iterate
     * @param iterator iterator
     */
    @Override
    public void iterateIndexableFilesIn(@NotNull VirtualFile file, @NotNull final ContentIterator iterator) {
        VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor() {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                if (!isInSet(file)) {
                    return false;
                }

                if (!file.isDirectory()) {
                    iterator.processFile(file);
                }

                return true;
            }
        });
    }
}
