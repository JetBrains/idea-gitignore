// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.YarnFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * YarnLanguage [IgnoreLanguage] definition.
 */
class YarnLanguage private constructor() : IgnoreLanguage("Yarn", "yarnignore", null, Icons.YARN) {

    companion object {
        val INSTANCE = YarnLanguage()
    }

    override val fileType
        get() = YarnFileType.INSTANCE
}
