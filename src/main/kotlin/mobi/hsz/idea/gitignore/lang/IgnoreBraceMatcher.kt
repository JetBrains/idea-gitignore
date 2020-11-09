// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import mobi.hsz.idea.gitignore.psi.IgnoreTypes

/**
 * Definition of [PairedBraceMatcher] class.
 */
class IgnoreBraceMatcher : PairedBraceMatcher {

    companion object {
        /** Array of definitions for brace pairs.  */
        private val PAIRS = arrayOf(BracePair(IgnoreTypes.BRACKET_LEFT, IgnoreTypes.BRACKET_RIGHT, false))
    }

    /**
     * Returns the array of definitions for brace pairs that need to be matched when
     * editing code in the language.
     *
     * @return the array of brace pair definitions.
     */
    override fun getPairs() = PAIRS

    /**
     * Returns true if paired rbrace should be inserted after lbrace of given type when lbrace is encountered before
     * contextType token.
     * It is safe to always return true, then paired brace will be inserted anyway.
     *
     * @param lbraceType  lbrace for which information is queried
     * @param contextType token type that follows lbrace
     * @return true / false as described
     */
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

    /**
     * Returns the start offset of the code construct which owns the opening structural brace at the specified offset.
     * For example, if the opening brace belongs to an 'if' statement, returns the start offset of the 'if' statement.
     *
     * @param file               the file in which brace matching is performed.
     * @param openingBraceOffset the offset of an opening structural brace.
     * @return the offset of corresponding code construct, or the same offset if not defined.
     */
    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}
