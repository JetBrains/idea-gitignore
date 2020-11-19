// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

/**
 * Wrapper function for showing [Notification].
 */
object Notify {

    private const val NOTIFICATION_GROUP_ID = ".ignore"

    /**
     * Shows [Notification].
     *
     * @param project  current project
     * @param title    notification title
     * @param content  notification text
     * @param type     notification type
     * @param listener optional listener
     */
    fun show(project: Project, title: String, content: String, type: NotificationType, listener: NotificationListener? = null) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, content, type, listener)
        Notifications.Bus.notify(notification, project)
    }
}
