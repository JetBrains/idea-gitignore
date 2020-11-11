// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.Commenter
import mobi.hsz.idea.gitignore.util.Constants

/**
 * Defines the support for "Comment with Line Comment" and "Comment with Block Comment" actions in a custom language.
 */
class IgnoreCommenter : Commenter {

    override fun getLineCommentPrefix() = Constants.HASH

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}
