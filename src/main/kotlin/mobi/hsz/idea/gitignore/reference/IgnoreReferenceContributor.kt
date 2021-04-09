// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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

    override fun registerReferenceProviders(psiReferenceRegistrar: PsiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IgnoreFile::class.java)),
            IgnoreReferenceProvider()
        )
    }

    private class IgnoreReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(psiElement: PsiElement, processingContext: ProcessingContext): Array<out PsiReference> =
            when (psiElement) {
                is IgnoreEntry -> IgnoreReferenceSet(psiElement).allReferences
                else -> PsiReference.EMPTY_ARRAY
            }
    }
}
