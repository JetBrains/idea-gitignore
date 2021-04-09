// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.EleventyFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * EleventyLanguage [IgnoreLanguage] definition.
 */
class EleventyLanguage private constructor() : IgnoreLanguage("Eleventy", "eleventyignore", null, Icons.ELEVENTY) {

    companion object {
        val INSTANCE = EleventyLanguage()
    }

    override val fileType
        get() = EleventyFileType.INSTANCE

    override val isVCS
        get() = false
}
