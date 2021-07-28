package mobi.hsz.idea.gitignore.foldableProjectView

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.installAndEnable
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.util.Notify

@Service
class FoldableProjectViewService {

    private val pluginId = PluginId.getId("ski.chrzanow.foldableprojectview")

    fun isAvailable() =
        PluginManagerCore.isPluginInstalled(pluginId)
            && PluginManagerCore.getPlugin(pluginId)?.isEnabled
            ?: false

    fun advertise(project: Project? = null) {
        if (isAvailable()) {
            return
        }

        Notify.show(
            project,
            IgnoreBundle.message("action.foldableProjectView.title"),
            IgnoreBundle.message("action.foldableProjectView.content"),
            type = NotificationType.INFORMATION,
        ) {
            addAction(NotificationAction.createSimple(IgnoreBundle.message("action.foldableProjectView.action")) {
                installAndEnable(project, setOf(pluginId), true) {}
            })
        }
    }
}
