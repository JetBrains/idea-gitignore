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
package mobi.hsz.idea.gitignore.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile

/**
 * PSI elements references contributor.
 */
class IgnoreReferenceContributor : PsiReferenceContributor() {

    /**
     * Registers new references provider for PSI element.
     *
     * @param psiReferenceRegistrar reference provider
     */
    override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IgnoreFile::class.java)),
            IgnoreReferenceProvider()
        )
    }

    private class IgnoreReferenceProvider : PsiReferenceProvider() {
        /**
         * Returns references for given @{link PsiElement}.
         *
         * @param psiElement        current element
         * @param processingContext context
         * @return [PsiReference] list
         */
        override fun getReferencesByElement(
            psiElement: PsiElement,
            processingContext: ProcessingContext
        ): Array<out PsiReference> =
            if (psiElement is IgnoreEntry) {
                IgnoreReferenceSet(psiElement).allReferences
            } else {
                PsiReference.EMPTY_ARRAY
            }
    }
}
