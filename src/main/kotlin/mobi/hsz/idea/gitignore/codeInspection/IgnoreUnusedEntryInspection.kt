// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
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
        val project = holder.project
        val manager = IgnoreManager.getInstance(project)

        return object : IgnoreVisitor() {
            override fun visitEntry(entry: IgnoreEntry) {
                val references = entry.references
                var resolved = true
                var previous = Int.MAX_VALUE

                for (reference in references) {
                    ProgressManager.checkCanceled()
                    if (reference is FileReferenceOwner) {
                        val fileReference = reference as PsiPolyVariantReference
                        val result = fileReference.multiResolve(false)
                        resolved = result.isNotEmpty() || previous > 0 && reference.getCanonicalText().endsWith("/*")
                        previous = result.size
                    }
                    if (!resolved) {
                        break
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
            private fun isEntryExcluded(entry: IgnoreEntry, project: Project): Boolean {
                val pattern = Glob.createPattern(entry) ?: return false
                val moduleRoot = Utils.getModuleRootForFile(entry.containingFile.virtualFile, project) ?: return false
                val files = MatcherUtil.getFilesForPattern(project, pattern)

                Utils.getExcludedRoots(project).forEach { root ->
                    files.forEach files@{ file ->
                        ProgressManager.checkCanceled()
                        if (!Utils.isUnder(file, root)) {
                            return@files
                        }
                        val path = Utils.getRelativePath(moduleRoot, root)
                        if (manager.matcher.match(pattern, path)) {
                            return false
                        }
                    }
                }

                return false
            }
        }
    }
}
