// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lexer.IgnoreLexerAdapter
import mobi.hsz.idea.gitignore.parser.IgnoreParser
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreTypes

/**
 * Defines the implementation of a parser for a custom language.
 */
class IgnoreParserDefinition : ParserDefinition {

    companion object {
        /** Whitespaces.  */
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)

        /** Regular comment started with #  */
        val COMMENTS = TokenSet.create(IgnoreTypes.COMMENT)

        /** Section comment started with ##  */
        val SECTIONS = TokenSet.create(IgnoreTypes.SECTION)

        /** Header comment started with ###  */
        val HEADERS = TokenSet.create(IgnoreTypes.HEADER)

        /** Negation element - ! in the beginning of the entry  */
        val NEGATIONS = TokenSet.create(IgnoreTypes.NEGATION)

        /** Brackets []  */
        val BRACKETS = TokenSet.create(IgnoreTypes.BRACKET_LEFT, IgnoreTypes.BRACKET_RIGHT)

        /** Slashes /  */
        val SLASHES = TokenSet.create(IgnoreTypes.SLASH)

        /** Syntax syntax:  */
        val SYNTAXES = TokenSet.create(IgnoreTypes.SYNTAX_KEY)

        /** All values - parts of paths  */
        val VALUES = TokenSet.create(IgnoreTypes.VALUE)

        /** Element type of the node describing a file in the specified language.  */
        val FILE = IFileElementType(Language.findInstance(IgnoreLanguage::class.java))
    }

    override fun createLexer(project: Project) = IgnoreLexerAdapter(null)

    override fun createParser(project: Project) = IgnoreParser()

    override fun getFileNodeType() = FILE

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = IgnoreTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider) = when (viewProvider.baseLanguage) {
        is IgnoreLanguage -> (viewProvider.baseLanguage as IgnoreLanguage).createFile(viewProvider)
        else -> IgnoreFile(viewProvider, IgnoreFileType.INSTANCE)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = SpaceRequirements.MAY
}
