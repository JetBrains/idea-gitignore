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

    /**
     * Returns the lexer for lexing files in the specified project. This lexer does not need to support incremental
     * relexing - it is always called for the entire file.
     *
     * @param project the project to which the lexer is connected.
     * @return the lexer instance.
     */
    override fun createLexer(project: Project) = IgnoreLexerAdapter(null)

    /**
     * Returns the parser for parsing files in the specified project.
     *
     * @param project the project to which the parser is connected.
     * @return the parser instance.
     */
    override fun createParser(project: Project) = IgnoreParser()

    /**
     * Returns the element type of the node describing a file in the specified language.
     *
     * @return the file node element type.
     */
    override fun getFileNodeType() = FILE

    /**
     * Returns the set of token types which are treated as whitespace by the PSI builder. Tokens of those types are
     * automatically skipped by PsiBuilder. Whitespace elements on the bounds of nodes built by PsiBuilder are
     * automatically excluded from the text range of the nodes.
     *
     ***It is strongly advised you return TokenSet
     * that only contains [com.intellij.psi.TokenType.WHITE_SPACE], which is suitable for all the languages unless
     * you really need to use special whitespace token**
     *
     * @return the set of whitespace token types.
     */
    override fun getWhitespaceTokens() = WHITE_SPACES

    /**
     * Returns the set of token types which are treated as comments by the PSI builder.
     * Tokens of those types are automatically skipped by PsiBuilder. Also, To Do patterns
     * are searched in the text of tokens of those types.
     *
     * @return the set of comment token types.
     */
    override fun getCommentTokens() = COMMENTS

    /**
     * Returns the set of element types which are treated as string literals. "Search in strings"
     * option in refactorings is applied to the contents of such tokens.
     *
     * @return the set of string literal element types.
     */
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    /**
     * Creates a PSI element for the specified AST node. The AST tree is a simple, semantic-free
     * tree of AST nodes which is built during the PsiBuilder parsing pass. The PSI tree is built
     * over the AST tree and includes elements of different types for different language constructs.
     *
     * @param node the node for which the PSI element should be returned.
     * @return the PSI element matching the element type of the AST node.
     */
    override fun createElement(node: ASTNode): PsiElement = IgnoreTypes.Factory.createElement(node)

    /**
     * Creates a PSI element for the specified virtual file.
     *
     * @param viewProvider virtual file.
     * @return the PSI file element.
     */
    override fun createFile(viewProvider: FileViewProvider) = when (viewProvider.baseLanguage) {
        is IgnoreLanguage -> (viewProvider.baseLanguage as IgnoreLanguage).createFile(viewProvider)
        else -> IgnoreFile(viewProvider, IgnoreFileType.INSTANCE)
    }

    /**
     * Checks if the specified two token types need to be separated by a space according to the language grammar. For
     * example, in Java two keywords are always separated by a space; a keyword and an opening parenthesis may be
     * separated or not separated. This is used for automatic whitespace insertion during AST modification operations.
     *
     * @param left  the first token to check.
     * @param right the second token to check.
     * @return the spacing requirements.
     *
     * @since 6.0
     */
    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = SpaceRequirements.MAY
}
