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

package mobi.hsz.idea.gitignore;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.ui.untrackFiles.UntrackFilesDialog;
import mobi.hsz.idea.gitignore.util.Notify;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentMap;

/**
 * ProjectComponent instance to handle {@link IgnoreManager.TrackedIgnoredListener} event
 * and display {@link Notification} about tracked and ignored files which invokes {@link UntrackFilesDialog}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7
 */
public class TrackedIgnoredFilesComponent extends AbstractProjectComponent
        implements IgnoreManager.TrackedIgnoredListener {
    /** Disable action event. */
    @NonNls
    private static final String DISABLE_ACTION = "#disable";

    /** {@link MessageBusConnection} instance. */
    private MessageBusConnection messageBus;

    /** {@link IgnoreSettings} instance. */
    private IgnoreSettings settings;

    /** Notification about tracked files was shown for current project. */
    private boolean notificationShown;

    /**
     * Constructor.
     *
     * @param project current project
     */
    protected TrackedIgnoredFilesComponent(@NotNull Project project) {
        super(project);
    }

    /** Component initialization method. */
    @Override
    public void initComponent() {
        settings = IgnoreSettings.getInstance();
        messageBus = myProject.getMessageBus().connect();
        messageBus.subscribe(IgnoreManager.TrackedIgnoredListener.TRACKED_IGNORED, this);
    }

    /** Component dispose method. */
    @Override
    public void disposeComponent() {
        if (messageBus != null) {
            messageBus.disconnect();
        }
    }

    /**
     * Returns component's name.
     *
     * @return component's name
     */
    @NotNull
    @Override
    public String getComponentName() {
        return "TrackedIgnoredFilesComponent";
    }

    /**
     * {@link IgnoreManager.TrackedIgnoredListener} method implementation to handle incoming files.
     *
     * @param files tracked and ignored files list
     */
    @Override
    public void handleFiles(@NotNull final ConcurrentMap<VirtualFile, VcsRoot> files) {
        if (!settings.isInformTrackedIgnored() || notificationShown || myProject.getBaseDir() == null) {
            return;
        }

        notificationShown = true;
        Notify.show(
                myProject,
                IgnoreBundle.message("notification.untrack.title", Utils.getVersion()),
                IgnoreBundle.message("notification.untrack.content"),
                NotificationType.INFORMATION,
                (notification, event) -> {
                    if (DISABLE_ACTION.equals(event.getDescription())) {
                        settings.setInformTrackedIgnored(false);
                    } else if (!myProject.isDisposed()) {
                        new UntrackFilesDialog(myProject, files).show();
                    }
                    notification.expire();
                }
        );
    }
}
