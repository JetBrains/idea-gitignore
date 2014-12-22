/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.daemon;

import com.intellij.notification.*;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor notification provider that shows donation balloon if it wasn't shown before.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6
 */
public class DonateNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    /** Notification key. */
    private static final Key<EditorNotificationPanel> KEY = Key.create(IgnoreBundle.message("daemon.donate.title"));

    /** Current project. */
    private final Project project;

    /** Notifications component. */
    private final EditorNotifications notifications;

    /** Plugin settings holder. */
    private final IgnoreSettings settings;

    /**
     * Builds a new instance of {@link DonateNotificationProvider}.
     *
     * @param project       current project
     * @param notifications notifications component
     */
    public DonateNotificationProvider(Project project, @NotNull EditorNotifications notifications) {
        this.project = project;
        this.notifications = notifications;
        this.settings = IgnoreSettings.getInstance();
    }

    /**
     * Gets notification key.
     *
     * @return notification key
     */
    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    /**
     * Creates notification panel for given file and checks if is allowed to show the notification.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(VirtualFile file, FileEditor fileEditor) {
        if (settings.isDonationShown()) {
            return null;
        }

        // TODO: Move to another place - EditorNotifications is pointless here
        NotificationGroup group = new NotificationGroup(IgnoreLanguage.GROUP, NotificationDisplayType.STICKY_BALLOON, true);
        Notification notification = group.createNotification(
                IgnoreBundle.message("daemon.donate.title"),
                IgnoreBundle.message("daemon.donate.content"),
                NotificationType.INFORMATION,
                NotificationListener.URL_OPENING_LISTENER
        );
        Notifications.Bus.notify(notification);
        settings.setDonationShown();

        return null;
    }
}
