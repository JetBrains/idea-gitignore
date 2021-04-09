// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.util.Glob
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Inspection tool that checks if entry has correct form in specific according to the specific [ ].
 */
class IgnoreIncorrectEntryInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : IgnoreVisitor() {
        override fun visitEntry(entry: IgnoreEntry) {
            var regex = entry.text
            if (IgnoreBundle.Syntax.GLOB == entry.syntax) {
                regex = Glob.createRegex(regex, false)
            }

            try {
                Pattern.compile(regex)
            } catch (e: PatternSyntaxException) {
                holder.registerProblem(entry, IgnoreBundle.message("codeInspection.incorrectEntry.message", e.description))
            }
        }
    }
}
