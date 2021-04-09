// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Util class to speed up and limit regex operation on the files paths.
 */
class MatcherUtil private constructor() {

    companion object {
        /**
         * Checks if given path contains all of the path parts.
         *
         * @param parts that should be contained in path
         * @param path  to check
         * @return path contains all parts
         */
        @Suppress("ReturnCount")
        fun matchAllParts(parts: Array<String?>?, path: String?): Boolean {
            var index = -1
            parts?.filterNotNull()?.forEach {
                index = path?.indexOf(it, index) ?: -1
                if (index == -1) {
                    return false
                }
            } ?: return false
            return true
        }

        /**
         * Checks if given path contains any of the path parts.
         *
         * @param parts that should be contained in path
         * @param path  to check
         * @return path contains any of the parts
         */
        fun matchAnyPart(parts: Array<String?>?, path: String?) = parts?.filterNotNull()?.any { path?.contains(it) ?: false } ?: false

        /**
         * Extracts alphanumeric parts from  [Pattern].
         *
         * @param pattern to handle
         * @return extracted parts
         */
        fun getParts(pattern: Pattern?): Array<String?> {
            if (pattern == null) {
                return arrayOfNulls(0)
            }
            val parts: MutableList<String?> = ArrayList()
            val sPattern = pattern.toString()
            var part = StringBuilder()
            var inSquare = false
            for (i in sPattern.indices) {
                val ch = sPattern[i]
                if (!inSquare && Character.isLetterOrDigit(ch)) {
                    part.append(sPattern[i])
                } else if (part.isNotEmpty()) {
                    parts.add(part.toString())
                    part = StringBuilder()
                }
                inSquare = ch != ']' && (ch == '[' || inSquare)
            }
            return parts.toTypedArray()
        }

        /**
         * Finds [VirtualFile] instances for the specific [Pattern] and caches them.
         *
         * @param project current project
         * @param pattern to handle
         * @return matched files list
         */
        fun getFilesForPattern(project: Project, pattern: Pattern): Collection<VirtualFile> {
            val parts = getParts(pattern).ifEmpty { return emptyList() }
            val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
            val scope = GlobalSearchScope.projectScope(project)
            val files = mutableSetOf<VirtualFile>()

            projectFileIndex.iterateContent {
                if (matchAnyPart(parts, it.name)) {
                    FilenameIndex.getVirtualFilesByName(project, it.name, scope).forEach { file ->
                        if (file.isValid && matchAllParts(parts, file.path)) {
                            files.add(file)
                        }
                    }
                }
                true
            }

            return files
        }
    }
}
