// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.PsiFileImpl
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
        } ?: throw RuntimeException("PsiFileBase: language.getParserDefinition() returned null for: $language")
    }

    /**
     * Passes the element to the specified visitor.
     *
     * @param visitor the visitor to pass the element to.
     */
    override fun accept(visitor: PsiElementVisitor) {
        visitor.visitFile(this)
    }

    /**
     * Returns current language.
     *
     * @return current [Language]
     */
    override fun getLanguage() = language

    /**
     * Returns the file type for the file.
     *
     * @return the file type instance.
     */
    override fun getFileType() = fileType

    /**
     * Checks if current file is the language outer file.
     *
     * @return is outer file
     */
    val isOuter
        get() = fileType.ignoreLanguage.getOuterFiles(project).contains(originalFile.virtualFile)

    /**
     * Returns @{link IgnoreFileType} string interpretation.
     *
     * @return string interpretation
     */
    override fun toString() = fileType.name
}
