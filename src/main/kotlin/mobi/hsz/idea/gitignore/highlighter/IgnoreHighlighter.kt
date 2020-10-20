// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import mobi.hsz.idea.gitignore.lang.IgnoreParserDefinition
import mobi.hsz.idea.gitignore.lexer.IgnoreLexerAdapter
import java.util.HashMap

/**
 * Syntax highlighter definition.
 */
class IgnoreHighlighter(
    private val project: Project?,
    private val virtualFile: VirtualFile?
) : SyntaxHighlighterBase() {

    companion object {
        /** Attributes map.  */
        private val ATTRIBUTES: Map<IElementType, TextAttributesKey> = HashMap()

        /* Binds parser definitions with highlighter colors. */
        init {
            fillMap(ATTRIBUTES, IgnoreParserDefinition.COMMENTS, IgnoreHighlighterColors.COMMENT)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SECTIONS, IgnoreHighlighterColors.SECTION)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.HEADERS, IgnoreHighlighterColors.HEADER)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.NEGATIONS, IgnoreHighlighterColors.NEGATION)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.BRACKETS, IgnoreHighlighterColors.BRACKET)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SLASHES, IgnoreHighlighterColors.SLASH)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SYNTAXES, IgnoreHighlighterColors.SYNTAX)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.VALUES, IgnoreHighlighterColors.VALUE)
        }
    }

    /**
     * Creates lexer adapter.
     *
     * @return lexer adapter
     */
    override fun getHighlightingLexer() = IgnoreLexerAdapter(project, virtualFile)

    /**
     * Gets highlighter text [TextAttributesKey] list using [IElementType] token.
     *
     * @param tokenType element type
     * @return attributes list
     */
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = pack(ATTRIBUTES[tokenType])
}
