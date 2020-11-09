// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.file.type.kind.FossilFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.outer.OuterIgnoreLoaderComponent.OuterFileFetcher
import mobi.hsz.idea.gitignore.util.Icons
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Fossil [IgnoreLanguage] definition.
 */
class FossilLanguage
private constructor() : IgnoreLanguage("Fossil",
    "ignore-glob",
    ".fossil-settings",
    Icons.FOSSIL,
    arrayOf( // Outer file fetched from the .fossil-settings/ignore-glob file.
        OuterFileFetcher { project ->
            ContainerUtil.createMaybeSingletonList(
                Utils.guessProjectDir(project)?.findFileByRelativePath(INSTANCE.vcsDirectory + "/" + INSTANCE.filename)
            )
        }
    )) {

    companion object {
        val INSTANCE = FossilLanguage()
    }

    override val fileType
        get() = FossilFileType.INSTANCE

    override val filename
        get() = extension

    override val isOuterFileSupported
        get() = true
}
