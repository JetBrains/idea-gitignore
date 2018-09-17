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
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Editor notification provider that checks if there is {@link GitLanguage#getFilename()}
 * in root directory and suggest to create one.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3.3
 */
public class MissingGitignoreNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    /** Notification key. */
    @NotNull
    private static final Key<EditorNotificationPanel> KEY = Key.create("MissingGitignoreNotificationProvider");

    /** Current project. */
    @NotNull
    private final Project project;

    /** Notifications component. */
    @NotNull
    private final EditorNotifications notifications;

    /** Plugin settings holder. */
    @NotNull
    private final IgnoreSettings settings;

    /**
     * Builds a new instance of {@link MissingGitignoreNotificationProvider}.
     *
     * @param project       current project
     * @param notifications notifications component
     */
    public MissingGitignoreNotificationProvider(@NotNull Project project, @NotNull EditorNotifications notifications) {
        this.project = project;
        this.notifications = notifications;
        this.settings = IgnoreSettings.getInstance();
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
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor) {
        // Break if feature is disabled in the Settings
        if (!settings.isMissingGitignore()) {
            return null;
        }
        // Break if user canceled previously this notification
        if (Properties.isIgnoreMissingGitignore(project)) {
            return null;
        }
        // Break if there is no Git directory in the project
        String vcsDirectory = GitLanguage.INSTANCE.getVcsDirectory();
        if (vcsDirectory == null) {
            return null;
        }

        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return null;
        }

        VirtualFile gitDirectory = baseDir.findChild(vcsDirectory);
        if (gitDirectory == null || !gitDirectory.isDirectory()) {
            return null;
        }
        // Break if there is Gitignore file already
        VirtualFile gitignoreFile = baseDir.findChild(GitLanguage.INSTANCE.getFilename());
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
        final IgnoreFileType fileType = GitFileType.INSTANCE;
        panel.setText(IgnoreBundle.message("daemon.missingGitignore"));
        panel.createActionLabel(IgnoreBundle.message("daemon.missingGitignore.create"), () -> {
            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
            if (directory != null) {
                try {
                    PsiFile file = new CreateFileCommandAction(project, directory, fileType).execute();
                    FileEditorManager.getInstance(project).openFile(file.getVirtualFile(), true);
                    new GeneratorDialog(project, file).show();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        panel.createActionLabel(IgnoreBundle.message("daemon.cancel"), () -> {
            Properties.setIgnoreMissingGitignore(project);
            notifications.updateAllNotifications();
        });

        try { // ignore if older SDK does not support panel icon
            Icon icon = fileType.getIcon();
            if (icon != null) {
                panel.icon(icon);
            }
        } catch (NoSuchMethodError ignored) {
        }

        return panel;
    }
}
