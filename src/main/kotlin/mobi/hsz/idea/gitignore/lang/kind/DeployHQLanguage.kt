// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.DeployHQFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * DeployHQLanguage [IgnoreLanguage] definition.
 */
class DeployHQLanguage private constructor() : IgnoreLanguage("DeployHQ", "deployignore", null, Icons.DEPLOYHQ) {

    companion object {
        val INSTANCE = DeployHQLanguage()
    }

    override val fileType
        get() = DeployHQFileType.INSTANCE
}
