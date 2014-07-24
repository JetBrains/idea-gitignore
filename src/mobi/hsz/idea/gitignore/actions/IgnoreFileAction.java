package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

public class IgnoreFileAction extends DumbAwareAction {
    public IgnoreFileAction() {
        super(GitignoreBundle.message("action.addToGitignore"), GitignoreBundle.message("action.addToGitignore.description"), Icons.FILE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile file = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiFile gitignore = Utils.getGitignoreFile(project);
        if (gitignore != null) {
            String path = getPath(project, file);
            Utils.openFile(project, gitignore);
            new AppendFileCommandAction(project, gitignore, path).execute();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final Project project = e.getProject();

        if (file == null || project == null || file.equals(project.getBaseDir())) {
            e.getPresentation().setVisible(false);
        }
    }

    private static String getPath(@NotNull Project project, @NotNull VirtualFile file) {
        String path = StringUtil.notNullize(Utils.getRelativePath(project.getBaseDir(), file));
        if (file.isDirectory() && !path.endsWith("/")) {
            path += "/";
        }
        return StringUtil.trimStart(path, "/");
    }
}
