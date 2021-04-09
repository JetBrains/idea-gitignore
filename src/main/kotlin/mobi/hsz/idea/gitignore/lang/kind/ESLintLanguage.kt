// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.ESLintFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * ESLint [IgnoreLanguage] definition.
 */
class ESLintLanguage private constructor() : IgnoreLanguage("ESLint", "eslintignore", null, Icons.ESLINT) {

    companion object {
        val INSTANCE = ESLintLanguage()
    }

    override val fileType
        get() = ESLintFileType.INSTANCE
}
