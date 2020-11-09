// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.psi.tree.IElementType
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import org.jetbrains.annotations.NonNls

/**
 * Token type definition.
 */
class IgnoreTokenType(@NonNls private val debugName: String) : IElementType(debugName, IgnoreLanguage.INSTANCE) {

    /**
     * String interpretation of the token type.
     *
     * @return string representation
     */
    override fun toString(): String = IgnoreBundle.messageOrDefault("tokenType.$debugName", "IgnoreTokenType." + super.toString())
}
