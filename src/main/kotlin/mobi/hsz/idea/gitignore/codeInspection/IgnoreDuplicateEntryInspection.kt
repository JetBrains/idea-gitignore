// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiFile
import com.intellij.util.containers.MultiMap
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor

/**
 * Inspection tool that checks if entries are duplicated by others.
 */
class IgnoreDuplicateEntryInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is IgnoreFile) {
            return null
        }

        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)
        val entries = MultiMap.create<String, IgnoreEntry>()
        file.acceptChildren(
            object : IgnoreVisitor() {
                override fun visitEntry(entry: IgnoreEntry) {
                    entries.putValue(entry.text, entry)
                    super.visitEntry(entry)
                }
            }
        )

        entries.entrySet().forEach { (_, value) ->
            val iterator = value.iterator()

            iterator.next()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                problemsHolder.registerProblem(
                    entry,
                    IgnoreBundle.message("codeInspection.duplicateEntry.message"),
                    IgnoreRemoveEntryFix(entry)
                )
            }
        }

        return problemsHolder.resultsArray
    }

    override fun runForWholeFile() = true
}
