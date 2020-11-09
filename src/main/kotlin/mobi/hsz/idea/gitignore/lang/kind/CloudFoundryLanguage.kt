// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.CloudFoundryFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * CloudFoundry [IgnoreLanguage] definition.
 */
class CloudFoundryLanguage private constructor() : IgnoreLanguage("CloudFoundry", "cfignore", null, Icons.CLOUD_FOUNDRY) {

    companion object {
        val INSTANCE = CloudFoundryLanguage()
    }

    override val fileType
        get() = CloudFoundryFileType.INSTANCE

    override val isVCS
        get() = false
}
