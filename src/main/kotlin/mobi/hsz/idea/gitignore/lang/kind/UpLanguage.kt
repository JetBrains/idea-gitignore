// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.UpFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * UPLanguage [IgnoreLanguage] definition.
 */
class UpLanguage private constructor() : IgnoreLanguage("Up", "upignore", null, Icons.UP) {

    companion object {
        val INSTANCE = UpLanguage()
    }

    override val fileType
        get() = UpFileType.INSTANCE

    override val isVCS
        get() = false
}
