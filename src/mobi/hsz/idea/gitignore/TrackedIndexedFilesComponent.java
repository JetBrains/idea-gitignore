/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import mobi.hsz.idea.gitignore.ui.untrackFiles.UntrackFilesDialog;
import mobi.hsz.idea.gitignore.util.Notify;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;

/**
 * {@link ProjectComponent} instance to handle {@link IgnoreManager.TrackedIndexedListener} event
 * and display {@link Notification} about tracked and ignored files which invokes {@link UntrackFilesDialog}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7
 */
public class TrackedIndexedFilesComponent extends AbstractProjectComponent implements IgnoreManager.TrackedIndexedListener {
    /** {@link MessageBusConnection} instance. */
    private MessageBusConnection messageBus;

    /**
     * Constructor.
     *
     * @param project current project
     */
    protected TrackedIndexedFilesComponent(Project project) {
        super(project);
    }

    /** Component initialization method. */
    @Override
    public void initComponent() {
        messageBus = myProject.getMessageBus().connect();
        messageBus.subscribe(IgnoreManager.TRACKED_INDEXED, this);
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
        return "TrackedIndexedFilesComponent";
    }

    /**
     * {@link IgnoreManager.TrackedIndexedListener} method implementation to handle incoming
     * files.
     * 
     * @param files tracked and ignored files list
     */
    @Override
    public void handleFiles(@NotNull final ArrayList<VirtualFile> files) {
        Notify.show(
                myProject,
                IgnoreBundle.message("notification.untrack.title", Utils.getVersion()),
                IgnoreBundle.message("notification.untrack.content"),
                NotificationType.WARNING,
                new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                        new UntrackFilesDialog(myProject, files).show();
                        notification.expire();
                    }
                }
        );
    }
}
