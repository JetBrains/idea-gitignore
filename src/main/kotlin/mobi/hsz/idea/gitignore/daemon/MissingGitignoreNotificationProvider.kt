// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDirectory
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.Properties
import mobi.hsz.idea.gitignore.util.Utils
import java.util.function.Function
import javax.swing.JComponent

/**
 * Editor notification provider that checks if there is .gitignore file in root directory and suggest to create one.
 */
class MissingGitignoreNotificationProvider(project: Project) : EditorNotificationProvider {

    private val notifications = EditorNotifications.getInstance(project)
    private val settings = service<IgnoreSettings>()

    /**
     * Creates a notification panel for given file and checks if is allowed to show the notification.
     *
     * @param project    current project
     * @param moduleRoot module root
     * @return notification panel
     */
    private fun createNotificationPanel(project: Project, moduleRoot: VirtualFile) = EditorNotificationPanel().apply {
        val fileType = GitFileType.INSTANCE

        text = IgnoreBundle.message("daemon.missingGitignore")
        createActionLabel(IgnoreBundle.message("daemon.missingGitignore.create")) {
            val directory = PsiManager.getInstance(project).findDirectory(moduleRoot)
            if (directory != null) {
                try {
                    val file = CreateFileCommandAction(project, directory, fileType).execute()
                    FileEditorManager.getInstance(project).openFile(file.virtualFile, true)
                    GeneratorDialog(project, file).show()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        }
        createActionLabel(IgnoreBundle.message("daemon.cancel")) {
            Properties.setIgnoreMissingGitignore(project)
            notifications.updateAllNotifications()
        }
        try { // ignore if older SDK does not support panel icon
            fileType.icon?.let { icon(it) }
        } catch (ignored: NoSuchMethodError) {
        }
    }

    override fun collectNotificationData(project: Project, file: VirtualFile): Function<in FileEditor, out JComponent?>? {
        if (DumbService.isDumb(project)) {
            return null
        }
        if (!settings.missingGitignore || Properties.isIgnoreMissingGitignore(project)) {
            return null
        }

        val vcsDirectory = GitLanguage.INSTANCE.vcsDirectory ?: return null
        val moduleRoot = Utils.getModuleRootForFile(file, project) ?: return null
        moduleRoot.findDirectory(vcsDirectory) ?: return null

        if (moduleRoot.findChild(GitLanguage.INSTANCE.filename) != null) {
            return null
        }
        return Function { createNotificationPanel(project, moduleRoot) }
    }
}
