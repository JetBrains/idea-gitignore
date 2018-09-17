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

package mobi.hsz.idea.gitignore.daemon;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Editor notification provider that informs about the attempt of the ignored file modification.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.8
 */
public class IgnoredEditingNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    /** Notification key. */
    @NotNull
    private static final Key<EditorNotificationPanel> KEY = Key.create("IgnoredEditingNotificationProvider");

    /** Current project. */
    @NotNull
    private final Project project;

    /** Notifications component. */
    @NotNull
    private final EditorNotifications notifications;

    /** Plugin settings holder. */
    @NotNull
    private final IgnoreSettings settings;

    /** {@link IgnoreManager} instance. */
    @NotNull
    private final IgnoreManager manager;

    /**
     * Builds a new instance of {@link IgnoredEditingNotificationProvider}.
     *
     * @param project       current project
     * @param notifications notifications component
     */
    public IgnoredEditingNotificationProvider(@NotNull Project project, @NotNull EditorNotifications notifications) {
        this.project = project;
        this.notifications = notifications;
        this.settings = IgnoreSettings.getInstance();
        this.manager = IgnoreManager.getInstance(project);
    }

    /**
     * Gets notification key.
     *
     * @return notification key
     */
    @NotNull
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
    public EditorNotificationPanel createNotificationPanel(@NotNull final VirtualFile file,
                                                           @NotNull FileEditor fileEditor) {
        if (!settings.isNotifyIgnoredEditing() || !manager.isFileIgnored(file) ||
                Properties.isDismissedIgnoredEditingNotification(project, file)) {
            return null;
        }

        final EditorNotificationPanel panel = new EditorNotificationPanel();

        panel.setText(IgnoreBundle.message("daemon.ignoredEditing"));
        panel.createActionLabel(IgnoreBundle.message("daemon.ok"), () -> {
            Properties.setDismissedIgnoredEditingNotification(project, file);
            notifications.updateAllNotifications();
        });

        try { // ignore if older SDK does not support panel icon
            panel.icon(Icons.IGNORE);
        } catch (NoSuchMethodError ignored) {
        }

        return panel;
    }
}
