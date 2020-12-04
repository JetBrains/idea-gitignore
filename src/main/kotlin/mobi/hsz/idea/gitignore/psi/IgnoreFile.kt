// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.PsiFileImpl
import mobi.hsz.idea.gitignore.IgnoreException
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage

/**
 * Base plugin file.
 */
class IgnoreFile(viewProvider: FileViewProvider, private val fileType: IgnoreFileType) : PsiFileImpl(viewProvider) {

    private val language = findLanguage(fileType.language, viewProvider)

    companion object {
        /**
         * Searches for the matching language in [FileViewProvider].
         *
         * @param baseLanguage language to look for
         * @param viewProvider current [FileViewProvider]
         * @return matched [Language]
         */
        private fun findLanguage(baseLanguage: Language, viewProvider: FileViewProvider): Language = viewProvider.languages.run {
            find { it.isKindOf(baseLanguage) }?.let { return it }
            find { it is IgnoreLanguage }?.let { return it }
            throw AssertionError("Language $baseLanguage doesn't participate in view provider $viewProvider: $this")
        }
    }

    init {
        LanguageParserDefinitions.INSTANCE.forLanguage(language).apply {
            init(fileNodeType, fileNodeType)
        } ?: throw IgnoreException("PsiFileBase: language.getParserDefinition() returned null for: $language")
    }

    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitFile(this)
    }

    override fun getLanguage() = language

    override fun getFileType() = fileType

    override fun toString() = fileType.name
}
