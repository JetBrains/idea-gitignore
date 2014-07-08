package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;

public class NewFileAction extends AnAction {
    public NewFileAction() {
        super(GitignoreBundle.message("action.newFile"), GitignoreBundle.message("action.newFile.description"), Icons.FILE);
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
            Messages.showInfoMessage(project, GitignoreBundle.message("action.newFile.exists"), GitignoreBundle.message("action.newFile.title"));
        }

        Utils.openFile(project, file);
        new GeneratorDialog(project, file).showDialog();
    }
}
