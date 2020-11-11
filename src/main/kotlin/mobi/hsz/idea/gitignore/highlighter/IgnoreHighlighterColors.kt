// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Contains highlighter attributes definitions.
 */
object IgnoreHighlighterColors {
    /** Default style for regular comment started with # */
    val COMMENT = TextAttributesKey.createTextAttributesKey("IGNORE.COMMENT")

    /** Default style for section comment started with ## */
    val SECTION = TextAttributesKey.createTextAttributesKey("IGNORE.SECTION")

    /** Default style for header comment started with ### */
    val HEADER = TextAttributesKey.createTextAttributesKey("IGNORE.HEADER")

    /** Default style for negation element - ! in the beginning of the entry */
    val NEGATION = TextAttributesKey.createTextAttributesKey("IGNORE.NEGATION")

    /** Default style for negation element - ! in the beginning of the entry */
    val BRACKET = TextAttributesKey.createTextAttributesKey("IGNORE.BRACKET")

    /** Default style for negation element - ! in the beginning of the entry */
    val SLASH = TextAttributesKey.createTextAttributesKey("IGNORE.SLASH")

    /** Default style for syntax element - syntax: */
    val SYNTAX = TextAttributesKey.createTextAttributesKey("IGNORE.SYNTAX")

    /** Default style for negation element - ! in the beginning of the entry */
    val VALUE = TextAttributesKey.createTextAttributesKey("IGNORE.VALUE")

    /** Default style for unused entry */
    val UNUSED = TextAttributesKey.createTextAttributesKey("IGNORE.UNUSED_ENTRY")
}
