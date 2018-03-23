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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * Provides extended {@link GlobalSearchScope} with additional ignore files (i.e. outer gitignore files).
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public class IgnoreSearchScope extends GlobalSearchScope {
    private IgnoreSearchScope(@NotNull Project project) {
        super(project);
    }

    /**
     * Returns {@link GlobalSearchScope#projectScope(Project)} instance united with additional files.
     *
     * @param project current project
     * @return extended instance of {@link GlobalSearchScope}
     */
    @NotNull
    public static GlobalSearchScope get(@NotNull Project project) {
        final IgnoreSearchScope scope = new IgnoreSearchScope(project);
        final HashSet<VirtualFile> files = ExternalIndexableSetContributor.getAdditionalFiles(project);
        return scope.uniteWith(GlobalSearchScope.filesScope(project, files));
    }

    @Override
    public int compare(@NotNull final VirtualFile file1, @NotNull final VirtualFile file2) {
        return 0;
    }

    @Override
    public boolean contains(@NotNull final VirtualFile file) {
        return file.getFileType() instanceof IgnoreFileType;
    }

    @Override
    public boolean isSearchInLibraries() {
        return true;
    }

    @Override
    public boolean isForceSearchingInLibrarySources() {
        return true;
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull final Module aModule) {
        return true;
    }

    @Override
    public boolean isSearchOutsideRootModel() {
        return true;
    }

    @NotNull
    @Override
    public GlobalSearchScope union(@NotNull SearchScope scope) {
        return this;
    }

    @NotNull
    @Override
    public SearchScope intersectWith(@NotNull SearchScope scope2) {
        return scope2;
    }
}
