// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.SourcegraphFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Sourcegraph [IgnoreLanguage] definition.
 */
class SourcegraphLanguage private constructor() : IgnoreLanguage("Sourcegraph", "ignore", ".sourcegraph", Icons.SOURCEGRAPH) {

    companion object {
        val INSTANCE = SourcegraphLanguage()
    }

    override val fileType
        get() = SourcegraphFileType.INSTANCE

    override val filename: String
        get() = extension
}
