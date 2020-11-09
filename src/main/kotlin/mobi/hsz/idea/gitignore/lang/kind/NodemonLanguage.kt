// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.NodemonFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Nodemon [IgnoreLanguage] definition.
 */
class NodemonLanguage private constructor() : IgnoreLanguage("Nodemon", "nodemonignore", null, Icons.NODEMON) {

    companion object {
        val INSTANCE = NodemonLanguage()
    }

    override val fileType
        get() = NodemonFileType.INSTANCE

    override val isVCS
        get() = false
}
