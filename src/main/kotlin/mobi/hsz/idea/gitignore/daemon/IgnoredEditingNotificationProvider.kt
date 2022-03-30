// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Icons
import java.util.function.Function
import javax.swing.JComponent

/**
 * Editor notification provider that informs about the attempt of the ignored file modification.
 */
class IgnoredEditingNotificationProvider(project: Project) : EditorNotificationProvider {

    private val notifications = EditorNotifications.getInstance(project)
    private val settings = service<IgnoreSettings>()
    private val manager = project.service<IgnoreManager>()
    private val changeListManager = ChangeListManager.getInstance(project)

    /**
     * Creates notification panel for given file and checks if is allowed to show the notification.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    private fun createNotificationPanel(file: VirtualFile) = when {
        !settings.notifyIgnoredEditing || !changeListManager.isIgnoredFile(file) && !manager.isFileIgnored(file) -> null
        else -> EditorNotificationPanel().apply {
            text = IgnoreBundle.message("daemon.ignoredEditing")
            icon(Icons.IGNORE)
        }
    }

    override fun collectNotificationData(project: Project, file: VirtualFile): Function<in FileEditor, out JComponent?> =
        Function { createNotificationPanel(file) }
}
