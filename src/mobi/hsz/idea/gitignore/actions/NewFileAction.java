package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.IdeView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
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
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final IdeView view = e.getRequiredData(LangDataKeys.IDE_VIEW);

        final PsiDirectory directory = view.getOrChooseDirectory();
        if (directory == null) {
            return;
        }

        PsiFile file = directory.findFile(GitignoreLanguage.FILENAME);
        if (file == null) {
            file = new CreateFileCommandAction(project, directory).execute().getResultObject();
        } else {
            Notifications.Bus.notify(new Notification(GitignoreLanguage.NAME,
                    GitignoreBundle.message("action.newFile.exists"), "in " + file.getVirtualFile().getPath(),
                    NotificationType.INFORMATION), project);
        }

        Utils.openFile(project, file);
        new GeneratorDialog(project, file).show();
    }

    @Override
    public void update(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final IdeView view = e.getData(LangDataKeys.IDE_VIEW);

        final PsiDirectory directory = view != null ? view.getOrChooseDirectory() : null;
        if (directory == null || project == null) {
            e.getPresentation().setVisible(false);
        }
    }
}
