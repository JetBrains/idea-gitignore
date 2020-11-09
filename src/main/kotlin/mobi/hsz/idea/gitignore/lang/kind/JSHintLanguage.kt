// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.JSHintFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * JSHint [IgnoreLanguage] definition.
 */
class JSHintLanguage private constructor() : IgnoreLanguage("JSHint", "jshintignore", null, Icons.JSHINT) {

    companion object {
        val INSTANCE = JSHintLanguage()
    }

    override val fileType
        get() = JSHintFileType.INSTANCE

    override val isVCS
        get() = false
}
