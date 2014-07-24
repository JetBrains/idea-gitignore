package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.ui.IgnoredTreeDialog;
import mobi.hsz.idea.gitignore.util.Icons;

public class ShowIgnoredAction extends DumbAwareAction {
    protected ShowIgnoredAction() {
        super(GitignoreBundle.message("action.showIgnored"), GitignoreBundle.message("action.showIgnored.description"), Icons.FILE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        if (file == null || !file.getLanguage().equals(GitignoreLanguage.INSTANCE)) {
            return;
        }

        new IgnoredTreeDialog(file).showDialog();
    }

    @Override
    public void update(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || file == null || !file.getName().equals(GitignoreLanguage.FILENAME)) {
            e.getPresentation().setVisible(false);
        }
    }
}
