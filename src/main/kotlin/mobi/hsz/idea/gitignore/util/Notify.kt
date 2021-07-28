// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

/**
 * Wrapper function for showing notification.
 */
object Notify {

    private const val NOTIFICATION_GROUP_ID = ".ignore"

    /**
     * Shows notification.
     *
     * @param project  current project
     * @param title    notification title
     * @param content  notification text
     * @param type     notification type
     */
    fun show(project: Project, title: String, content: String? = null, type: NotificationType, block: (Notification.() -> Unit)? = null) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID).run {
                when (content) {
                    null -> createNotification(title, type)
                    else -> createNotification(title, content, type)
                }
            }
        block?.invoke(notification)
        Notifications.Bus.notify(notification, project)
    }
}
