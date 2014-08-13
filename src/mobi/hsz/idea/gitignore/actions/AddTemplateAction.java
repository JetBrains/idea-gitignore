package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreFile;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Icons;

public class AddTemplateAction extends AnAction {
    public AddTemplateAction() {
        super(GitignoreBundle.message("action.addTemplate"), GitignoreBundle.message("action.addTemplate.description"), Icons.FILE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final PsiFile file = e.getRequiredData(CommonDataKeys.PSI_FILE);

        if (!(file instanceof GitignoreFile)) {
            return;
        }

        new GeneratorDialog(project, file).show();
    }

    @Override
    public void update(AnActionEvent e) {
        final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if (file != null && !(file instanceof GitignoreFile)) {
            e.getPresentation().setVisible(false);
        }
    }
}
