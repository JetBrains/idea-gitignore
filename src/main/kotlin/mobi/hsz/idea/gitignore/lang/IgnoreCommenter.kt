// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.Commenter
import mobi.hsz.idea.gitignore.util.Constants

/**
 * Defines the support for "Comment with Line Comment" and "Comment with Block Comment" actions in a custom language.
 */
class IgnoreCommenter : Commenter {

    /**
     * Returns the string which prefixes a line comment in the language, or null if the language does not support line comments.
     *
     * @return the line comment text, or null.
     */
    override fun getLineCommentPrefix() = Constants.HASH

    /**
     * Returns the string which marks the beginning of a block comment in the language,
     * or null if the language does not support block comments.
     *
     * @return the block comment start text, or null.
     */
    override fun getBlockCommentPrefix(): String? = null

    /**
     * Returns the string which marks the end of a block comment in the language,
     * or null if the language does not support block comments.
     *
     * @return the block comment end text, or null.
     */
    override fun getBlockCommentSuffix(): String? = null

    /**
     * Returns the string which marks the commented beginning of a block comment in the language,
     * or null if the language does not support block comments.
     *
     * @return the commented block comment start text, or null.
     */
    override fun getCommentedBlockCommentPrefix(): String? = null

    /**
     * Returns the string which marks the commented end of a block comment in the language,
     * or null if the language does not support block comments.
     *
     * @return the commented block comment end text, or null.
     */
    override fun getCommentedBlockCommentSuffix(): String? = null
}
