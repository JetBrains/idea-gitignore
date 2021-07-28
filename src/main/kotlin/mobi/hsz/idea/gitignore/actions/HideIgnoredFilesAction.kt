// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.foldableProjectView.FoldableProjectViewService
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Action that hides or show ignored files in the project tree view.
 */
class HideIgnoredFilesAction : DumbAwareAction(getPresentationText(), "", Icons.IGNORE) {

    private val foldableProjectViewService = service<FoldableProjectViewService>()

    companion object {
        fun getPresentationText() = message("action.hideIgnoredVisibility")
    }

    override fun update(e: AnActionEvent) {
        e.presentation.apply {
            isVisible = !foldableProjectViewService.isAvailable()
            text = getPresentationText()
        }
    }

    override fun actionPerformed(e: AnActionEvent) = foldableProjectViewService.advertise(e.project)

}
