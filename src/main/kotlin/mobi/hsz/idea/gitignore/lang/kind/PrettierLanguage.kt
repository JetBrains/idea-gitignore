// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.PrettierFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Prettier [IgnoreLanguage] definition.
 */
class PrettierLanguage private constructor() : IgnoreLanguage("Prettier", "prettierignore", null, Icons.PRETTIER) {

    companion object {
        val INSTANCE = PrettierLanguage()
    }

    override val fileType
        get() = PrettierFileType.INSTANCE
}
