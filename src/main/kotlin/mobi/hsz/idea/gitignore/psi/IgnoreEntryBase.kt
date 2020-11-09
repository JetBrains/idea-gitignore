// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.psi.PsiElement
import mobi.hsz.idea.gitignore.IgnoreBundle
import java.util.regex.Pattern

interface IgnoreEntryBase : PsiElement {

    /**
     * Checks if current element is negated.
     *
     * @return is negated
     */
    val isNegated: Boolean

    /**
     * Returns current element's syntax.
     *
     * @return current syntax
     */
    val syntax: IgnoreBundle.Syntax

    /**
     * Returns current value.
     *
     * @return value
     */
    val value: String

    /**
     * Returns current pattern.
     *
     * @return pattern
     */
    val pattern: Pattern?
}
