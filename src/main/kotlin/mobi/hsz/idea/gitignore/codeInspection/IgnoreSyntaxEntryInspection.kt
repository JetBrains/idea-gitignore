// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor

/**
 * Inspection tool that checks if syntax entry has correct value.
 */
class IgnoreSyntaxEntryInspection : LocalInspectionTool() {

    /**
     * Checks if syntax entry has correct value.
     *
     * @param holder     where visitor will register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return not-null visitor for this inspection
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) = object : IgnoreVisitor() {
        override fun visitSyntax(syntax: IgnoreSyntax) {
            if (!(syntax.containingFile.language as IgnoreLanguage).isSyntaxSupported) {
                return
            }

            IgnoreBundle.Syntax.values().forEach {
                if (it.toString() == syntax.value.text) {
                    return
                }
            }

            holder.registerProblem(syntax, IgnoreBundle.message("codeInspection.syntaxEntry.message"), IgnoreSyntaxEntryFix(syntax))
        }
    }
}
