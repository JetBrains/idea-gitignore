// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.MatcherUtil
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Inspection tool that checks if entries are unused - does not cover any file or directory.
 */
class IgnoreUnusedEntryInspection : LocalInspectionTool() {

    /**
     * Checks if entries are related to any file.
     *
     * @param holder     where visitor will register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return not-null visitor for this inspection
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val matcher = holder.project.service<IgnoreMatcher>()

        return object : IgnoreVisitor() {
            override fun visitEntry(entry: IgnoreEntry) {
                val references = entry.references
                var resolved = true
                var previous = Int.MAX_VALUE

                references.forEach { reference ->
                    ProgressManager.checkCanceled()
                    if (reference is FileReferenceOwner) {
                        val fileReference = reference as PsiPolyVariantReference
                        val result = fileReference.multiResolve(false)
                        resolved = result.isNotEmpty() || previous > 0 && reference.getCanonicalText().endsWith("/*")
                        previous = result.size
                    }
                    if (!resolved) {
                        return@forEach
                    }
                }
                if (!resolved && !isEntryExcluded(entry, holder.project)) {
                    (entry.parent as IgnoreFile).containingDirectory?.virtualFile?.findFileByRelativePath(entry.text)
                        ?: holder.registerProblem(
                            entry,
                            IgnoreBundle.message("codeInspection.unusedEntry.message"),
                            IgnoreRemoveEntryFix(entry)
                        )
                }
                super.visitEntry(entry)
            }

            /**
             * Checks if given [IgnoreEntry] is excluded in the current [Project].
             *
             * @param entry   Gitignore entry
             * @param project current project
             * @return entry is excluded in current project
             */
            @Suppress("ReturnCount")
            private fun isEntryExcluded(entry: IgnoreEntry, project: Project): Boolean {
                val pattern = Glob.createPattern(entry) ?: return false
                val moduleRoot = Utils.getModuleRootForFile(entry.containingFile.virtualFile, project) ?: return false
                val files = MatcherUtil.getFilesForPattern(project, pattern)

                getExcludedRoots(project).forEach { root ->
                    files.forEach files@{ file ->
                        ProgressManager.checkCanceled()
                        if (!isUnder(file, root)) {
                            return@files
                        }
                        val path = Utils.getRelativePath(moduleRoot, root)
                        if (matcher.match(pattern, path)) {
                            return false
                        }
                    }
                }

                return false
            }
        }
    }

    /**
     * Searches for excluded roots in given [Project].
     *
     * @param project current project
     * @return list of excluded roots
     */
    fun getExcludedRoots(project: Project) =
        ModuleManager.getInstance(project).modules.map {
            ModuleRootManager.getInstance(it).modifiableModel
        }.filter { !it.isDisposed }.map {
            it.excludeRoots.toList()
        }.flatten()

    /**
     * Checks if file is under given directory.
     *
     * @param file      file
     * @param directory directory
     * @return file is under directory
     */
    @Suppress("ReturnCount")
    fun isUnder(file: VirtualFile, directory: VirtualFile): Boolean {
        if (directory == file) {
            return true
        }
        var parent = file.parent
        while (parent != null) {
            if (directory == parent) {
                return true
            }
            parent = parent.parent
        }
        return false
    }
}
