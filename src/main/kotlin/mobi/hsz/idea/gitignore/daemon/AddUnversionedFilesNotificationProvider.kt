// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Properties
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.ExternalExec

/**
 * Editor notification provider that suggests to add unversioned files to the .gitignore file.
 */
class AddUnversionedFilesNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {

    private val settings = IgnoreSettings.getInstance()
    private val unignoredFiles = mutableListOf<String>()
    private val handledMap = ContainerUtil.createWeakKeyWeakValueMap<VirtualFile?, Boolean?>()

    companion object {
        private val KEY = Key.create<EditorNotificationPanel?>("AddUnversionedFilesNotificationProvider")
    }

    override fun getKey() = KEY

    /**
     * Creates notification panel for given file and checks if is allowed to show the notification.
     * Only [GitLanguage] is currently supported.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project) = when {
        !settings.addUnversionedFiles -> null
        Properties.isAddUnversionedFiles(project) -> null
        Properties.isAddUnversionedFiles(project) -> null
        handledMap[file] != null -> null
        IgnoreBundle.obtainLanguage(file).run { this == null || !isVCS || this !is GitLanguage } -> null
        else -> {
            unignoredFiles.clear()
            unignoredFiles.addAll(ExternalExec.getUnignoredFiles(GitLanguage.INSTANCE, project, file))
            when {
                unignoredFiles.isEmpty() -> null
                else -> createPanel(project)
            }
        }
    }

    /**
     * Creates notification panel.
     *
     * @param project current project
     * @return notification panel
     */
    private fun createPanel(project: Project) = EditorNotificationPanel().apply {
        val notifications = EditorNotifications.getInstance(project)

        text = IgnoreBundle.message("daemon.addUnversionedFiles")
        createActionLabel(IgnoreBundle.message("daemon.addUnversionedFiles.create")) {
            val projectDir = Utils.guessProjectDir(project) ?: return@createActionLabel
            val virtualFile = projectDir.findChild(GitLanguage.INSTANCE.filename)

            virtualFile?.run { PsiManager.getInstance(project).findFile(this) }?.let {
                val content = mutableSetOf(StringUtil.join(unignoredFiles, Constants.NEWLINE))
                try {
                    AppendFileCommandAction(project, it, content, ignoreDuplicates = true).execute()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
                handledMap[virtualFile] = true
                notifications.updateAllNotifications()
            }
        }
        createActionLabel(IgnoreBundle.message("daemon.cancel")) {
            Properties.setAddUnversionedFiles(project)
            notifications.updateAllNotifications()
        }
        try { // ignore if older SDK does not support panel icon
            GitFileType.INSTANCE.icon?.let { icon(it) }
        } catch (ignored: NoSuchMethodError) {
        }
    }
}
