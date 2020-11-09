// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.PerforceFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Perforce [IgnoreLanguage] definition.
 */
class PerforceLanguage private constructor() : IgnoreLanguage("Perforce", "p4ignore", null, Icons.PERFORCE) {

    companion object {
        val INSTANCE = PerforceLanguage()
    }

    override val fileType
        get() = PerforceFileType.INSTANCE
}
