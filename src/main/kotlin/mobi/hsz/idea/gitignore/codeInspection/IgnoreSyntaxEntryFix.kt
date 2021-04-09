// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax

/**
 * QuickFix action that invokes [mobi.hsz.idea.gitignore.codeInsight.SyntaxCompletionContributor]
 * on the given [IgnoreSyntax] element.
 */
class IgnoreSyntaxEntryFix(syntax: IgnoreSyntax) : LocalQuickFixAndIntentionActionOnPsiElement(syntax) {

    override fun invoke(project: Project, file: PsiFile, editor: Editor?, startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreSyntax) {
            editor?.run {
                startElement.value.let {
                    selectionModel.setSelection(it.textOffset, it.textOffset + it.textLength)
                }
                CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, this)
            }
        }
    }

    override fun startInWriteAction() = false

    override fun getText(): String = IgnoreBundle.message("quick.fix.syntax.entry")

    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
