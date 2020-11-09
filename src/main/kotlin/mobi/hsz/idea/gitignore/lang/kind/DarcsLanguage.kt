// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.kind.DarcsFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Darcs [IgnoreLanguage] definition.
 */
class DarcsLanguage private constructor() : IgnoreLanguage("Darcs", "boringignore", ".darcs", Icons.DARCS) {

    companion object {
        val INSTANCE = DarcsLanguage()
    }

    override val fileType
        get() = DarcsFileType.INSTANCE

    override val defaultSyntax
        get() = IgnoreBundle.Syntax.REGEXP
}
