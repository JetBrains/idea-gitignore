// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Action that hides or show ignored files in the project tree view.
 */
class HideIgnoredFilesAction : AnAction(getPresentationText(), "", Icons.IGNORE) {

    companion object {
        val settings = IgnoreSettings.getInstance()

        fun getPresentationText() = when {
            settings.hideIgnoredFiles -> IgnoreBundle.message("action.showIgnoredVisibility")
            else -> IgnoreBundle.message("action.hideIgnoredVisibility")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        settings.hideIgnoredFiles = !settings.hideIgnoredFiles
        templatePresentation.apply {
            text = getPresentationText()
        }
    }
}
