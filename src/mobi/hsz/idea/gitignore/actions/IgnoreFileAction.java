package mobi.hsz.idea.gitignore.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class IgnoreFileAction extends DumbAwareAction {
    VirtualFile gitignoreFile;

    public IgnoreFileAction() {
        this(null);
    }

    public IgnoreFileAction(@Nullable VirtualFile virtualFile) {
        super(GitignoreBundle.message("action.addToGitignore"), GitignoreBundle.message("action.addToGitignore.description"), Icons.FILE);
        gitignoreFile = virtualFile;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile[] files = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiFile gitignore = null;
        if (gitignoreFile != null) {
            gitignore = PsiManager.getInstance(project).findFile(gitignoreFile);
        }
        if (gitignore == null) {
            gitignore = Utils.getGitignoreFile(project, null, true);
        }

        if (gitignore != null) {
            Set<String> paths = new HashSet<String>();
            for (VirtualFile file : files) {
                String path = getPath(gitignore.getVirtualFile().getParent(), file);
                if (path.isEmpty()) {
                    Notifications.Bus.notify(new Notification(GitignoreLanguage.NAME,
                            GitignoreBundle.message("action.ignoreFile.addError", Utils.getRelativePath(project.getBaseDir(), file)),
                            "to " + Utils.getRelativePath(project.getBaseDir(), gitignore.getVirtualFile()),
                            NotificationType.ERROR), project);
                } else {
                    paths.add(path);
                }
            }
            Utils.openFile(project, gitignore);
            new AppendFileCommandAction(project, gitignore, paths).execute();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile[] files = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        final Project project = e.getProject();

        if (project == null || (files.length == 1 && files[0].equals(project.getBaseDir()))) {
            e.getPresentation().setVisible(false);
        }
    }

    private static String getPath(@NotNull VirtualFile root, @NotNull VirtualFile file) {
        String path = StringUtil.notNullize(Utils.getRelativePath(root, file));
        path = StringUtil.escapeChar(path, '[');
        path = StringUtil.escapeChar(path, ']');
        return StringUtil.trimStart(path, "/");
    }
}
