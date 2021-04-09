// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor

/**
 * Inspection tool that checks if entry is relative.
 */
class IgnoreRelativeEntryInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is IgnoreFile) {
            return null
        }

        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)
        file.acceptChildren(
            object : IgnoreVisitor() {
                override fun visitEntry(entry: IgnoreEntry) {
                    val path = entry.text.replace("\\\\(.)".toRegex(), "$1")
                    if (path.contains("./")) {
                        problemsHolder.registerProblem(
                            entry,
                            IgnoreBundle.message("codeInspection.relativeEntry.message"),
                            IgnoreRelativeEntryFix(entry)
                        )
                    }
                    super.visitEntry(entry)
                }
            }
        )
        return problemsHolder.resultsArray
    }

    override fun runForWholeFile() = true
}
