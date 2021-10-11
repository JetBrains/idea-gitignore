// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Creates a group of [NewFileAction] instances.
 */
class NewFileGroupAction : DefaultActionGroup(), DumbAware {

    init {
        isPopup = true
        templatePresentation.apply {
            @Suppress("DialogTitleCapitalization")
            text = IgnoreBundle.message("action.newFile.group")
            icon = Icons.IGNORE
        }

        (listOf(GitLanguage.INSTANCE) + IgnoreBundle.LANGUAGES).toSet().forEach {
            add(
                object : NewFileAction(it.fileType) {
                    init {
                        templatePresentation.apply {
                            text = IgnoreBundle.message("action.newFile", it.filename, it.id)
                            description = IgnoreBundle.message("action.newFile.description", it.id)
                            icon = it.fileType.icon
                        }
                    }
                }
            )
        }
    }
}
