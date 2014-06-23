package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.lang.GitignoreLanguage;

public class NewGitignoreFileAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

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
            file = GitignoreTemplatesFactory.createFromTemplate(directory);
        } else {
            Messages.showInfoMessage(project, "Gitignore file already exists.", "Gitignore Plugin");
        }

        FileEditorManager.getInstance(project).openFile(file.getVirtualFile(), true);
    }
}
