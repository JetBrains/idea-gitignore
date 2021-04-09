// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Gitignore [IgnoreLanguage] definition.
 */
class GitLanguage private constructor() : IgnoreLanguage("Git", "gitignore", ".git", Icons.GIT) {

    companion object {
        val INSTANCE = GitLanguage()
    }

    override val fileType
        get() = GitFileType.INSTANCE

    override val isVCS
        get() = true
}
