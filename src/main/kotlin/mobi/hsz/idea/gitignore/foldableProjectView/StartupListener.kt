package mobi.hsz.idea.gitignore.foldableProjectView

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

class StartupListener : AppLifecycleListener, ProjectManagerListener {

    private val settings = service<IgnoreSettings>()
    private val foldableProjectViewService = service<FoldableProjectViewService>()

    override fun welcomeScreenDisplayed() = advertise()

    override fun projectOpened(project: Project) = advertise(project)

    private fun advertise(project: Project? = null) {
        if (!settings.foldableProjectViewAdvertiseShown) {
            settings.foldableProjectViewAdvertiseShown = true
        } else {
            return
        }

        foldableProjectViewService.advertise(project)
    }
}
