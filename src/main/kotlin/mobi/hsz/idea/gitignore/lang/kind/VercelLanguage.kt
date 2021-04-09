// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.VercelFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * VercelLanguage [IgnoreLanguage] definition.
 */
class VercelLanguage private constructor() : IgnoreLanguage("Vercel", "vercelignore", null, Icons.VERCEL) {

    companion object {
        val INSTANCE = VercelLanguage()
    }

    override val fileType
        get() = VercelFileType.INSTANCE
}
