// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.NuxtJSFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * NuxtJS [IgnoreLanguage] definition.
 */
class NuxtJSLanguage private constructor() : IgnoreLanguage("NuxtJS", "nuxtignore", null, Icons.NUXTJS) {

    companion object {
        val INSTANCE = NuxtJSLanguage()
    }

    override val fileType
        get() = NuxtJSFileType.INSTANCE

    override val isVCS
        get() = false
}
