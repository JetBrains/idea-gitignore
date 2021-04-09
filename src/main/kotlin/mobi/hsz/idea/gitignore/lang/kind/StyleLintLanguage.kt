// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.StyleLintFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * StyleLint [StyleLintLanguage] definition.
 */
class StyleLintLanguage private constructor() : IgnoreLanguage("StyleLint", "stylelintignore", null, Icons.STYLELINT) {

    companion object {
        val INSTANCE = StyleLintLanguage()
    }

    override val fileType
        get() = StyleLintFileType.INSTANCE

    override val isVCS
        get() = false
}
