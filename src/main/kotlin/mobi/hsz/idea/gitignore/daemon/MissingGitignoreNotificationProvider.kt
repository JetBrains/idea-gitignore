// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.Properties
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Editor notification provider that checks if there is .gitignore file in root directory and suggest to create one.
 */
class MissingGitignoreNotificationProvider(project: Project) : EditorNotifications.Provider<EditorNotificationPanel?>() {

    private val notifications = EditorNotifications.getInstance(project)
    private val settings = service<IgnoreSettings>()

    companion object {
        private val KEY = Key.create<EditorNotificationPanel?>("MissingGitignoreNotificationProvider")
    }

    override fun getKey() = KEY

    /**
     * Creates notification panel for given file and checks if is allowed to show the notification.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project): EditorNotificationPanel? = when {
        !settings.missingGitignore -> null
        Properties.isIgnoreMissingGitignore(project) -> null
        else -> {
            val vcsDirectory = GitLanguage.INSTANCE.vcsDirectory
            val moduleRoot = Utils.getModuleRootForFile(file, project)
            val gitignoreFile = moduleRoot?.findChild(GitLanguage.INSTANCE.filename)

            when {
                vcsDirectory == null -> null
                moduleRoot == null -> null
                gitignoreFile != null -> null
                moduleRoot.findChild(vcsDirectory)?.isDirectory ?: true -> null
                else -> createPanel(project, moduleRoot)
            }
        }
    }

    /**
     * Creates notification panel.
     *
     * @param project    current project
     * @param moduleRoot module root
     * @return notification panel
     */
    private fun createPanel(project: Project, moduleRoot: VirtualFile): EditorNotificationPanel {
        val fileType = GitFileType.INSTANCE
        return EditorNotificationPanel().apply {
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
    }
}
