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
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MissingGitignoreNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    private static final Key<EditorNotificationPanel> KEY = Key.create("Create .gitignore");
    private final Project project;
    private final EditorNotifications notifications;

    public MissingGitignoreNotificationProvider(Project project, @NotNull EditorNotifications notifications) {
        this.project = project;
        this.notifications = notifications;
    }

    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(VirtualFile file, FileEditor fileEditor) {
        if (Properties.getIgnoreMissingGitignore(project)) {
            return null;
        }
        VirtualFile gitDirectory = project.getBaseDir().findChild(".git");
        if (gitDirectory == null || !gitDirectory.isDirectory()) {
            return null;
        }
        VirtualFile gitignoreFile = project.getBaseDir().findChild(GitignoreLanguage.FILENAME);
        if (gitignoreFile != null) {
            return null;
        }

        return createPanel(project);
    }

    private EditorNotificationPanel createPanel(@NotNull final Project project) {
        final EditorNotificationPanel panel = new EditorNotificationPanel();
        panel.setText("Missing .gitignore file in GIT project");
        panel.icon(Icons.FILE);
        panel.createActionLabel("Create .gitignore", new Runnable() {
            @Override
            public void run() {
                PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
                if (directory != null) {
                    PsiFile file = new CreateFileCommandAction(project, directory).execute().getResultObject();
                    FileEditorManager.getInstance(project).openFile(file.getVirtualFile(), true);
                    new GeneratorDialog(project, file).showDialog();
                }
            }
        });
        panel.createActionLabel("Abort", new Runnable() {
            @Override
            public void run() {
                Properties.setIgnoreMissingGitignore(project, true);
                notifications.updateAllNotifications();
            }
        });
        return panel;
    }
}
