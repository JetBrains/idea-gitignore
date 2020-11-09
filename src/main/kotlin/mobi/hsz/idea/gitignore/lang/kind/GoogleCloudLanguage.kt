// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.GoogleCloudFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Google Cloud [IgnoreLanguage] definition.
 */
class GoogleCloudLanguage private constructor() : IgnoreLanguage("Google Cloud", "gcloudignore", null, Icons.GCLOUD) {

    companion object {
        val INSTANCE = GoogleCloudLanguage()
    }

    override val fileType
        get() = GoogleCloudFileType.INSTANCE

    override val isVCS
        get() = false
}
