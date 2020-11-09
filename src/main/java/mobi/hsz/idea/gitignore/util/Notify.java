// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper function for showing {@link Notification}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7
 */
public class Notify {
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup(
            IgnoreBundle.message("notification.group"),
            NotificationDisplayType.BALLOON,
            true,
            ToolWindowId.PROJECT_VIEW
    );

    /**
     * Shows {@link Notification} in ".ignore plugin" group.
     *
     * @param project   current project
     * @param title     notification title
     * @param content   notification text
     * @param type      notification type
     */
    public static void show(@NotNull Project project, @NotNull String title, @NotNull String content,
                            @NotNull NotificationType type) {
        show(project, title, content, NOTIFICATION_GROUP, type, null);
    }

    /**
     * Shows {@link Notification}.
     *
     * @param project  current project
     * @param title    notification title
     * @param group    notification group
     * @param content  notification text
     * @param type     notification type
     * @param listener optional listener
     */
    public static void show(@NotNull Project project, @NotNull String title, @NotNull String content,
                            @NotNull NotificationGroup group, @NotNull NotificationType type,
                            @Nullable NotificationListener listener) {
        Notification notification = group.createNotification(title, content, type, listener);
        Notifications.Bus.notify(notification, project);
    }
}
