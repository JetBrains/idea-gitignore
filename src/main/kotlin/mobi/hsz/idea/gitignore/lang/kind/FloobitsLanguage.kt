// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.FloobitsFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Floobits [IgnoreLanguage] definition.
 */
class FloobitsLanguage private constructor() : IgnoreLanguage("Floobits", "flooignore", null, Icons.FLOOBITS) {

    companion object {
        val INSTANCE = FloobitsLanguage()
    }

    override val fileType
        get() = FloobitsFileType.INSTANCE

    override val isVCS
        get() = false
}
