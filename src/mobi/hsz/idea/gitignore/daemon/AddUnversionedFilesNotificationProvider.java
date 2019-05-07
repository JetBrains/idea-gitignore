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
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Constants;
import mobi.hsz.idea.gitignore.util.Properties;
import mobi.hsz.idea.gitignore.util.exec.ExternalExec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Editor notification provider that suggests to add unversioned files to the .gitignore file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.4
 */
public class AddUnversionedFilesNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    /** Notification key. */
    @NotNull
    private static final Key<EditorNotificationPanel> KEY = Key.create("AddUnversionedFilesNotificationProvider");

    /** Notifications component. */
    @NotNull
    private final EditorNotifications notifications;

    /** Plugin settings holder. */
    @NotNull
    private final IgnoreSettings settings;

    /** List of unignored files. */
    @NotNull
    private final List<String> unignoredFiles = ContainerUtil.newArrayList();

    /** Map to obtain if file was handled. */
    private final Map<VirtualFile, Boolean> handledMap = ContainerUtil.createWeakKeyWeakValueMap();

    /**
     * Builds a new instance of {@link AddUnversionedFilesNotificationProvider}.
     *
     * @param notifications notifications component
     */
    public AddUnversionedFilesNotificationProvider(@NotNull EditorNotifications notifications) {
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
     * Only {@link GitLanguage} is currently supported.
     *
     * @param file       current file
     * @param fileEditor current file editor
     * @return created notification panel
     */
    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor,
                                                           @NotNull Project project) {
        // Break if feature is disabled in the Settings
        if (!settings.isAddUnversionedFiles()) {
            return null;
        }
        // Break if user canceled previously this notification
        if (Properties.isAddUnversionedFiles(project)) {
            return null;
        }

        if (handledMap.get(file) != null) {
            return null;
        }

        final IgnoreLanguage language = IgnoreBundle.obtainLanguage(file);
        if (language == null || !language.isVCS() || !(language instanceof GitLanguage)) {
            return null;
        }

        unignoredFiles.clear();
        unignoredFiles.addAll(ExternalExec.getUnignoredFiles(GitLanguage.INSTANCE, project, file));
        if (unignoredFiles.isEmpty()) {
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
        panel.setText(IgnoreBundle.message("daemon.addUnversionedFiles"));
        panel.createActionLabel(IgnoreBundle.message("daemon.addUnversionedFiles.create"), () -> {
            final VirtualFile projectDir = ProjectUtil.guessProjectDir(project);
            if (projectDir == null) {
                return;
            }
            final VirtualFile virtualFile = projectDir.findChild(GitLanguage.INSTANCE.getFilename());
            final PsiFile file = virtualFile != null ? PsiManager.getInstance(project).findFile(virtualFile) : null;
            if (file != null) {
                final String content = StringUtil.join(unignoredFiles, Constants.NEWLINE);

                try {
                    new AppendFileCommandAction(project, file, content, true, false)
                            .execute();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                handledMap.put(virtualFile, true);
                notifications.updateAllNotifications();
            }
        });
        panel.createActionLabel(IgnoreBundle.message("daemon.cancel"), () -> {
            Properties.setAddUnversionedFiles(project);
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
