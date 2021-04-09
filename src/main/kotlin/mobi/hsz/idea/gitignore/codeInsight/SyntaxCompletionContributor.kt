// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInsight

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax

/**
 * Class provides completion feature for [mobi.hsz.idea.gitignore.psi.IgnoreTypes.SYNTAX] element.
 */
class SyntaxCompletionContributor : CompletionContributor() {

    companion object {
        /** Allowed values for the completion. */
        private val SYNTAX_ELEMENTS = mutableListOf<LookupElementBuilder>()

        init {
            IgnoreBundle.Syntax.values().mapTo(SYNTAX_ELEMENTS) { LookupElementBuilder.create(it.toString().toLowerCase()) }
        }
    }

    init {
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf(PsiElement::class.java),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
                    parameters.position.apply {
                        if (parent is IgnoreSyntax && prevSibling != null) {
                            result.addAllElements(SYNTAX_ELEMENTS)
                        }
                    }
                }
            }
        )
    }
}
