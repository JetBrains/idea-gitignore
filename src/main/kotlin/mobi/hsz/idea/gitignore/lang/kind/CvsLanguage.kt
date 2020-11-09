// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.CvsFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Cvs [IgnoreLanguage] definition.
 */
class CvsLanguage private constructor() : IgnoreLanguage("Cvs", "cvsignore", null, Icons.CVS) {

    companion object {
        val INSTANCE = CvsLanguage()
    }

    override val fileType
        get() = CvsFileType.INSTANCE
}
