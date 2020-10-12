/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
