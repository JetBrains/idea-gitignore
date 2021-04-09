// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.kind.MercurialFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Mercurial [IgnoreLanguage] definition.
 */
class MercurialLanguage private constructor() : IgnoreLanguage("Mercurial", "hgignore", ".hg", Icons.MERCURIAL) {

    companion object {
        val INSTANCE = MercurialLanguage()
    }

    override val fileType
        get() = MercurialFileType.INSTANCE

    override val isSyntaxSupported
        get() = true

    override val defaultSyntax
        get() = IgnoreBundle.Syntax.REGEXP
}
