// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.StylintFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Stylint [StylintLanguage] definition.
 */
class StylintLanguage private constructor() : IgnoreLanguage("Stylint", "stylintignore", null, Icons.STYLINT) {

    companion object {
        val INSTANCE = StylintLanguage()
    }

    override val fileType
        get() = StylintFileType.INSTANCE
}
