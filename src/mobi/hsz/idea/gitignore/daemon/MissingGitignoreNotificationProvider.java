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

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.file.type.gitignore.GitignoreFileType;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Editor notification provider that checks if there is {@link GitignoreLanguage#getFilename()} in root directory
 * and suggest to create one.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3.3
 */
public class MissingGitignoreNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    /** Notification key. */
    private static final Key<EditorNotificationPanel> KEY = Key.create(IgnoreBundle.message("daemon.missingGitignore.create"));

    /** Current project. */
    private final Project project;

    /** Notifications component. */
    private final EditorNotifications notifications;

    /** Plugin settings holder. */
    private final IgnoreSettings settings;

    /**
     * Builds a new instance of {@link MissingGitignoreNotificationProvider}.
     *
     * @param project       current project
     * @param notifications notifications component
     */
    public MissingGitignoreNotificationProvider(Project project, @NotNull EditorNotifications notifications) {
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
        // Break if feature is disabled in the Settings
        if (!settings.isMissingGitignore()) {
            return null;
        }
        // Break if user canceled previously this notification
        if (Properties.isIgnoreMissingGitignore(project)) {
            return null;
        }
        // Break if there is no Git directory in the project
        VirtualFile gitDirectory = project.getBaseDir().findChild(GitignoreLanguage.INSTANCE.getGitDirectory());
        if (gitDirectory == null || !gitDirectory.isDirectory()) {
            return null;
        }
        // Break if there is Gitignore file already
        VirtualFile gitignoreFile = project.getBaseDir().findChild(GitignoreLanguage.INSTANCE.getFilename());
        if (gitignoreFile != null) {
            return null;
        }

        return createPanel(project);
    }

    /**
     * Creates notification panel.
     *
     * @param project current project
     * @return notification panel
     */
    private EditorNotificationPanel createPanel(@NotNull final Project project) {
        final EditorNotificationPanel panel = new EditorNotificationPanel();
        final IgnoreFileType fileType = GitignoreFileType.INSTANCE;
        panel.setText(IgnoreBundle.message("daemon.missingGitignore"));
        panel.createActionLabel(IgnoreBundle.message("daemon.missingGitignore.create"), new Runnable() {
            @Override
            public void run() {
                PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
                if (directory != null) {
                    PsiFile file = new CreateFileCommandAction(project, directory, fileType).execute().getResultObject();
                    FileEditorManager.getInstance(project).openFile(file.getVirtualFile(), true);
                    new GeneratorDialog(project, file).show();
                }
            }
        });
        panel.createActionLabel(IgnoreBundle.message("global.cancel"), new Runnable() {
            @Override
            public void run() {
                Properties.setIgnoreMissingGitignore(project);
                notifications.updateAllNotifications();
            }
        });

        try { // ignore if older SDK does not support panel icon
            Icon icon = fileType.getIcon();
            if (icon != null) {
                panel.icon(icon);
            }
        } catch (NoSuchMethodError ignored) {}

        return panel;
    }
}
