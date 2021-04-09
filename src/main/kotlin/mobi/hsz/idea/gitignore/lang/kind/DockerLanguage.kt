// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.DockerFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Docker [IgnoreLanguage] definition.
 */
class DockerLanguage private constructor() : IgnoreLanguage("Docker", "dockerignore", null, Icons.DOCKER) {

    companion object {
        val INSTANCE = DockerLanguage()
    }

    override val fileType
        get() = DockerFileType.INSTANCE

    override val isVCS
        get() = false
}
