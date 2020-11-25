// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

/**
 * Provides extended [GlobalSearchScope] with additional ignore files (i.e. outer gitignore files).
 */
class IgnoreSearchScope private constructor(project: Project) : GlobalSearchScope(project) {

    override fun contains(file: VirtualFile) = file.fileType is IgnoreFileType

    override fun isSearchInLibraries() = true

    override fun isForceSearchingInLibrarySources() = true

    override fun isSearchInModuleContent(aModule: Module) = true

    override fun union(scope: SearchScope) = this

    override fun intersectWith(scope2: SearchScope) = scope2

    companion object {
        /**
         * Returns [GlobalSearchScope.projectScope] instance united with additional files.
         *
         * @param project current project
         * @return extended instance of [GlobalSearchScope]
         */
        operator fun get(project: Project) = IgnoreSearchScope(project)
    }
}
