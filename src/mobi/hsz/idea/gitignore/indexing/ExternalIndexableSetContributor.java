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

package mobi.hsz.idea.gitignore.indexing;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IndexableSetContributor;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * IndexedRootsProvider implementation that provides additional paths to index - like external/global ignore files.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public class ExternalIndexableSetContributor extends IndexableSetContributor {
    /** Empty set. */
    private static final Set<VirtualFile> EMPTY_SET = Collections.emptySet();

    /** Cached additional paths set. */
    private static final Map<Project, HashSet<VirtualFile>> CACHE = ContainerUtil.newConcurrentMap();

    /**
     * Returns additional files located outside of the current project that should be indexed.
     *
     * @param project current project
     * @return additional files
     */
    @NotNull
    public static HashSet<VirtualFile> getAdditionalFiles(@NotNull Project project) {
        final HashSet<VirtualFile> files = new HashSet<>();

        if (CACHE.containsKey(project)) {
            files.addAll(ContainerUtil.filter(CACHE.get(project), VirtualFile::isValid));
        } else {
            for (IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
                final IgnoreFileType fileType = language.getFileType();
                if (language.isOuterFileSupported()) {
                    for (VirtualFile file : language.getOuterFiles(project, true)) {
                        if (file == null || !file.isValid()) {
                            continue;
                        }
                        if (!(file.getFileType() instanceof IgnoreFileType) && !file.getFileType().equals(fileType)) {
                            IgnoreManager.associateFileType(file.getName(), fileType);
                        }

                        files.add(file);
                    }
                }
            }
        }

        CACHE.put(project, files);
        return files;
    }

    /**
     * @param project Project to check
     * @return an additional project-dependent set of {@link VirtualFile} instances to index, the returned set should
     * not contain nulls or invalid files
     */
    @NotNull
    @Override
    public Set<VirtualFile> getAdditionalProjectRootsToIndex(@NotNull Project project) {
        return getAdditionalFiles(project);
    }

    /**
     * Not implemented. Returns {@link #EMPTY_SET}.
     *
     * @return {@link #EMPTY_SET}
     */
    @NotNull
    @Override
    public Set<VirtualFile> getAdditionalRootsToIndex() {
        return EMPTY_SET;
    }

    /** Removes invalidated projects from the {@link #CACHE} map. */
    public static void invalidateDisposedProjects() {
        for (Project project : CACHE.keySet()) {
            if (project.isDisposed()) {
                CACHE.remove(project);
            }
        }
    }

    /**
     * Removes cached files for the given project.
     *
     * @param project current project
     */
    public static void invalidateCache(@NotNull Project project) {
        CACHE.remove(project);
    }
}
