// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.ChefFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Chef [IgnoreLanguage] definition.
 */
class ChefLanguage private constructor() : IgnoreLanguage("Chef", "chefignore", null, Icons.CHEF) {

    companion object {
        val INSTANCE = ChefLanguage()
    }

    override val fileType
        get() = ChefFileType.INSTANCE

    override val isVCS
        get() = false
}
