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
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.codeInspection.IgnoreSyntaxEntryFix
import mobi.hsz.idea.gitignore.FilesIndexCacheProjectComponent
import mobi.hsz.idea.gitignore.IgnoreManager
import com.intellij.psi.PsiReference
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiDirectory
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.codeInspection.IgnoreRemoveEntryFix
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Inspection tool that checks if entries are unused - does not cover any file or directory.
 *
 * @author Jakub Chrzanowski <jakub></jakub>@hsz.mobi>
 * @since 0.5
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
        val cache = FilesIndexCacheProjectComponent.getInstance(project)
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
                        resolved = result.size > 0 || previous > 0 && reference.getCanonicalText().endsWith("/*")
                        previous = result.size
                    }
                    if (!resolved) {
                        break
                    }
                }
                if (!resolved) {
                    if (!isEntryExcluded(entry, holder.project)) {
                        val directory = (entry.parent as IgnoreFile).containingDirectory
                        val file = directory?.virtualFile?.findFileByRelativePath(entry.text)
                        if (file == null) {
                            holder.registerProblem(
                                entry,
                                IgnoreBundle.message("codeInspection.unusedEntry.message"),
                                IgnoreRemoveEntryFix(entry)
                            )
                        }
                    }
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
                val moduleRoot = Utils.getModuleRootForFile(
                    entry.containingFile.virtualFile,
                    project
                )
                val files = cache.getFilesForPattern(project, pattern)
                if (moduleRoot == null) {
                    return false
                }
                for (root in Utils.getExcludedRoots(project)) {
                    for (file in files) {
                        ProgressManager.checkCanceled()
                        if (!Utils.isUnder(file, root)) {
                            continue
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
