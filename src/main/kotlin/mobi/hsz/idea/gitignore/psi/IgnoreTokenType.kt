// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.psi.tree.IElementType
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import org.jetbrains.annotations.NonNls

/**
 * Token type definition.
 */
class IgnoreTokenType(@NonNls val myDebugName: String) : IElementType(myDebugName, IgnoreLanguage.INSTANCE) {

    override fun toString() = IgnoreBundle.messageOrDefault("tokenType.$myDebugName", "IgnoreTokenType." + super.toString()).orEmpty()
}
