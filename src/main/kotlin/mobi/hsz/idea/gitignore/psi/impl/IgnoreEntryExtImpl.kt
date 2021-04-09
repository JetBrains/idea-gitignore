// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import mobi.hsz.idea.gitignore.IgnoreBundle.Syntax
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.psi.IgnoreElementImpl
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreEntryFile
import mobi.hsz.idea.gitignore.psi.IgnoreNegation
import mobi.hsz.idea.gitignore.psi.IgnoreTypes
import mobi.hsz.idea.gitignore.util.Glob

/**
 * Custom [IgnoreElementImpl] implementation.
 */
abstract class IgnoreEntryExtImpl(node: ASTNode) : IgnoreElementImpl(node), IgnoreEntry {

    /**
     * Checks if the first child is negated - i.e. `!file.txt` entry.
     *
     * @return first child is negated
     */
    override val isNegated
        get() = firstChild is IgnoreNegation

    /**
     * Checks if current entry is a directory - i.e. `dir/`.
     *
     * @return is directory
     */
    val isDirectory
        get() = this is IgnoreEntryFile

    /**
     * Returns element syntax.
     *
     * @return syntax
     */
    override val syntax: Syntax
        get() {
            var previous = prevSibling
            while (previous != null) {
                if (previous.node.elementType == IgnoreTypes.SYNTAX) {
                    Syntax.find((previous as IgnoreSyntaxImpl).value.text)?.let {
                        return it
                    }
                }
                previous = previous.prevSibling
            }
            return (containingFile.language as IgnoreLanguage).defaultSyntax
        }

    /**
     * Returns entry value without leading `!` if entry is negated.
     *
     * @return entry value without `!` negation sign
     */
    override val value
        get() = text.takeIf { !isNegated } ?: StringUtil.trimStart(text, "!")

    /**
     * Returns entries pattern.
     *
     * @return pattern
     */
    override val pattern
        get() = Glob.createPattern(this)
}
