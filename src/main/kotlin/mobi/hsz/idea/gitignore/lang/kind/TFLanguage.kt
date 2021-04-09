// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.TFFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * TFLanguage [IgnoreLanguage] definition.
 */
class TFLanguage private constructor() : IgnoreLanguage("Team Foundation", "tfignore", null, Icons.TF) {

    companion object {
        val INSTANCE = TFLanguage()
    }

    override val fileType
        get() = TFFileType.INSTANCE
}
