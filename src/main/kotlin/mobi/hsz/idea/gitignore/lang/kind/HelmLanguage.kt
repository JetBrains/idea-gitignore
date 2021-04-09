// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.HelmFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Kubernetes Helm [IgnoreLanguage] definition.
 */
class HelmLanguage private constructor() : IgnoreLanguage("Kubernetes Helm", "helmignore", null, Icons.HELM) {

    companion object {
        val INSTANCE = HelmLanguage()
    }

    override val fileType
        get() = HelmFileType.INSTANCE

    override val isVCS
        get() = false
}
