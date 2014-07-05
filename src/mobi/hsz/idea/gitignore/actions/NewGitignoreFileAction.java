package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;

public class NewGitignoreFileAction extends AnAction {
    public NewGitignoreFileAction() {
        super(".gitignore", "Create new .gitignore file", Icons.FILE);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        DataContext dataContext = event.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }

        final PsiDirectory directory = view.getOrChooseDirectory();
        if (directory == null) {
            return;
        }

        PsiFile file = directory.findFile(GitignoreLanguage.FILENAME);
        if (file == null) {
            file = new CreateFileCommandAction(project, directory).execute().getResultObject();
        } else {
            Messages.showInfoMessage(project, "Gitignore file already exists.", "Gitignore Plugin");
        }

        Utils.openFile(project, file);
        new GeneratorDialog(project, file).showDialog();
    }
}
