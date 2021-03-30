// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Icons
import mobi.hsz.idea.gitignore.util.Properties

/**
 * Editor notification provider that informs about the attempt of the ignored file modification.
 */
class IgnoredEditingNotificationProvider(project: Project) : EditorNotifications.Provider<EditorNotificationPanel?>() {

    private val notifications = EditorNotifications.getInstance(project)
    private val settings = service<IgnoreSettings>()
    private val manager = project.service<IgnoreManager>()
    private val changeListManager = ChangeListManager.getInstance(project)

    companion object {
        private val KEY = Key.create<EditorNotificationPanel?>("IgnoredEditingNotificationProvider")
    }

    override fun getKey() = KEY

    /**
     * Creates notification panel for given file and checks if is allowed to show the notification.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    override fun createNotificationPanel(file: VirtualFile, fileEditor: FileEditor, project: Project) = when {
        !settings.notifyIgnoredEditing || Properties.isDismissedIgnoredEditingNotification(project, file)
            || !changeListManager.isIgnoredFile(file) && !manager.isFileIgnored(file) -> null
        else -> EditorNotificationPanel().apply {
            text = IgnoreBundle.message("daemon.ignoredEditing")
            createActionLabel(IgnoreBundle.message("daemon.ok")) {
                Properties.setDismissedIgnoredEditingNotification(project, file)
                notifications.updateAllNotifications()
            }
            try { // ignore if older SDK does not support panel icon
                icon(Icons.IGNORE)
            } catch (ignored: NoSuchMethodError) {
            }
        }
    }
}
