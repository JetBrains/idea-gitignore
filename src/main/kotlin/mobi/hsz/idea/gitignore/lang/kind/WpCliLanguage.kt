// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.WpCliFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * WP-CLI [IgnoreLanguage] definition.
 */
class WpCliLanguage private constructor() : IgnoreLanguage("WP-CLI", "distignore", null, Icons.GCLOUD) {

    companion object {
        val INSTANCE = WpCliLanguage()
    }

    override val fileType
        get() = WpCliFileType.INSTANCE
}
