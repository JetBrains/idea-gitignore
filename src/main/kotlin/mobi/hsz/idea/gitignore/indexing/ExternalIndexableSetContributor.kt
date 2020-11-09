// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor
import com.jetbrains.rd.util.concurrentMapOf
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType
import java.util.HashSet

/**
 * IndexedRootsProvider implementation that provides additional paths to index - like external/global ignore files.
 */
class ExternalIndexableSetContributor : IndexableSetContributor() {

    companion object {
        /** Empty set.  */
        private val EMPTY_SET: Set<VirtualFile> = emptySet()

        /** Cached additional paths set.  */
        private val CACHE = concurrentMapOf<Project, HashSet<VirtualFile>>()

        /**
         * Returns additional files located outside of the current project that should be indexed.
         *
         * @param project current project
         * @return additional files
         */
        @JvmStatic
        fun getAdditionalFiles(project: Project): HashSet<VirtualFile> {
            val files = HashSet<VirtualFile>()

            if (CACHE.containsKey(project)) {
                CACHE[project]?.let { files.addAll(it.filter(VirtualFile::isValid)) }
            } else {
                IgnoreBundle.LANGUAGES.forEach { language ->
                    val fileType = language.fileType
                    if (language.isOuterFileSupported) {
                        language.getOuterFiles(project, true).forEach outerFiles@{ file ->
                            if (file == null || !file.isValid) {
                                return@outerFiles
                            }
                            if (fileType !is GitExcludeFileType && file.fileType !is IgnoreFileType && file.fileType != fileType) {
                                IgnoreManager.associateFileType(file.name, fileType)
                            }
                            files.add(file)
                        }
                    }
                }
            }
            CACHE[project] = files
            return files
        }

        /** Removes invalidated projects from the [.CACHE] map.  */
        @JvmStatic
        fun invalidateDisposedProjects() {
            CACHE.keys
                .asSequence()
                .filter { it.isDisposed }
                .forEach { CACHE.remove(it) }
        }

        /**
         * Removes cached files for the given project.
         *
         * @param project current project
         */
        @JvmStatic
        fun invalidateCache(project: Project) {
            CACHE.remove(project)
        }
    }

    /**
     * @param project Project to check
     * @return an additional project-dependent set of [VirtualFile] instances to index, the returned set should not contain nulls
     * or invalid files
     */
    override fun getAdditionalProjectRootsToIndex(project: Project) = getAdditionalFiles(project)

    /**
     * Not implemented. Returns [.EMPTY_SET].
     *
     * @return [.EMPTY_SET]
     */
    override fun getAdditionalRootsToIndex() = EMPTY_SET
}
