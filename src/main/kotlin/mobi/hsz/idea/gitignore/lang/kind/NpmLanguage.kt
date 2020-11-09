// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.NpmFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Npm [IgnoreLanguage] definition.
 */
class NpmLanguage private constructor() : IgnoreLanguage("Npm", "npmignore", null, Icons.NPM) {

    companion object {
        val INSTANCE = NpmLanguage()
    }

    override val fileType
        get() = NpmFileType.INSTANCE

    override val isVCS
        get() = false
}
