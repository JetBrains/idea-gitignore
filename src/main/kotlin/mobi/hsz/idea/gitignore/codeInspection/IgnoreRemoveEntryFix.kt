// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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

    override fun invoke(project: Project, file: PsiFile, editor: Editor?, startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreEntry) {
            removeCrlf(startElement)
            startElement.delete()
        }
    }

    private fun removeCrlf(startElement: PsiElement) {
        (
            TreeUtil.findSibling(startElement.node, IgnoreTypes.CRLF) ?: TreeUtil.findSiblingBackward(
                startElement.node,
                IgnoreTypes.CRLF
            )
            )?.psi?.delete()
    }

    override fun getText(): String = IgnoreBundle.message("quick.fix.remove.entry")

    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
