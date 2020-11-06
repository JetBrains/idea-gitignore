// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.TreeUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreTypes

/**
 * QuickFix action that removes specified entry handled by code inspections like [IgnoreCoverEntryInspection],
 * [IgnoreDuplicateEntryInspection], [IgnoreUnusedEntryInspection].
 */
class IgnoreRemoveEntryFix(entry: IgnoreEntry) : LocalQuickFixAndIntentionActionOnPsiElement(entry) {

    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    override fun getText(): String = IgnoreBundle.message("quick.fix.remove.entry")

    /**
     * Handles QuickFix action invoked on [IgnoreEntry].
     *
     * @param project      the [Project] containing the working file
     * @param file         the [PsiFile] containing handled entry
     * @param startElement the [IgnoreEntry] that will be removed
     * @param endElement   the [PsiElement] which is ignored in invoked action
     */
    override fun invoke(project: Project, file: PsiFile, editor: Editor?, startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreEntry) {
            removeCrlf(startElement)
            startElement.delete()
        }
    }

    /**
     * Shorthand method for removing CRLF element.
     *
     * @param startElement working PSI element
     */
    private fun removeCrlf(startElement: PsiElement) {
        (TreeUtil.findSibling(startElement.node, IgnoreTypes.CRLF) ?: TreeUtil.findSiblingBackward(
            startElement.node,
            IgnoreTypes.CRLF
        ))?.psi?.delete()
    }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
