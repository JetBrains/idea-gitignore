// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file.type.kind

import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.kind.BazaarLanguage

/**
 * Describes Bazaar file type.
 */
class BazaarFileType : IgnoreFileType(BazaarLanguage.INSTANCE) {

    companion object {
        val INSTANCE = BazaarFileType()
    }
}
