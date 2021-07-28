// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.installAndEnable
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.util.Icons
import mobi.hsz.idea.gitignore.util.Notify

/**
 * Action that hides or show ignored files in the project tree view.
 */
class HideIgnoredFilesAction : DumbAwareAction(getPresentationText(), "", Icons.IGNORE) {

    companion object {
        private val pluginId = PluginId.getId("ski.chrzanow.foldableprojectview")

        fun isFoldableProjectViewAvailable() =
            PluginManagerCore.isPluginInstalled(pluginId)
                && PluginManagerCore.getPlugin(pluginId)?.isEnabled
                ?: false

        fun getPresentationText() = message("action.hideIgnoredVisibility")
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = !isFoldableProjectViewAvailable()
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.presentation.apply {
            isVisible = !isFoldableProjectViewAvailable()
            text = getPresentationText()
        }

        e.project?.takeIf { !isFoldableProjectViewAvailable() }?.let { project ->
            Notify.show(
                project,
                message("action.foldableProjectView.title"),
                type = NotificationType.INFORMATION,
            ) {
                addAction(NotificationAction.createSimple(message("action.foldableProjectView.action")) {
                    installAndEnable(project, setOf(pluginId), true) {}
                })
            }
        }
    }
}
