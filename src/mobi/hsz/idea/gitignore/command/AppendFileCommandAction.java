package mobi.hsz.idea.gitignore.command;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

public class AppendFileCommandAction extends WriteCommandAction<PsiFile> {
    private final Project project;
    private final PsiFile file;
    private final String content;

    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull String content) {
        super(project, file);
        this.project = project;
        this.file = file;
        this.content = content;
    }

    @Override
    protected void run(@NotNull Result<PsiFile> result) throws Throwable {
        if (content.isEmpty() || content.equals("\n")) {
            return;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            for (PsiElement element : file.getChildren()) {
                if (content.equals(element.getText())) {
                    Notifications.Bus.notify(new Notification(GitignoreLanguage.NAME,
                            GitignoreBundle.message("action.appendFile.entryExists", content),
                            "in " + Utils.getRelativePath(project.getBaseDir(), file.getVirtualFile()),
                            NotificationType.WARNING), project);
                    return;
                }
            }
            document.insertString(document.getTextLength(), "\n" + content.replace("\r", ""));
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
