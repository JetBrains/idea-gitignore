// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.TokeiFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * TokeiLanguage [IgnoreLanguage] definition.
 */
class TokeiLanguage private constructor() : IgnoreLanguage("Tokei", "tokeignore", null, Icons.TOKEI) {

    companion object {
        val INSTANCE = TokeiLanguage()
    }

    override val fileType
        get() = TokeiFileType.INSTANCE
}
