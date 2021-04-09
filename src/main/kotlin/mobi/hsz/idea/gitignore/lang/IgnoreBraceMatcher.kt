// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
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
        private val PAIRS = arrayOf(BracePair(IgnoreTypes.BRACKET_LEFT, IgnoreTypes.BRACKET_RIGHT, false))
    }

    override fun getPairs() = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}
