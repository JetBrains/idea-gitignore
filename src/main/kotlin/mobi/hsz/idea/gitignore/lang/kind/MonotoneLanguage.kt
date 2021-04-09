// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.kind.MonotoneFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Monotone [IgnoreLanguage] definition.
 */
class MonotoneLanguage private constructor() : IgnoreLanguage("Monotone", "mtn-ignore", "_MTN", Icons.MONOTONE) {

    companion object {
        val INSTANCE = MonotoneLanguage()
    }

    override val fileType
        get() = MonotoneFileType.INSTANCE

    override val defaultSyntax
        get() = IgnoreBundle.Syntax.REGEXP
}
