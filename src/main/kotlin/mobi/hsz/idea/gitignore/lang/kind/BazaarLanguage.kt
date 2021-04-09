// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.BazaarFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Bazaar [IgnoreLanguage] definition.
 */
class BazaarLanguage private constructor() : IgnoreLanguage("Bazaar", "bzrignore", ".bzr", Icons.BAZAAR) {

    companion object {
        val INSTANCE = BazaarLanguage()
    }

    override val fileType
        get() = BazaarFileType.INSTANCE
}
