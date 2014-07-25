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

import java.util.ArrayList;
import java.util.List;

public class AppendFileCommandAction extends WriteCommandAction<PsiFile> {
    private final Project project;
    private final PsiFile file;
    private final List<String> content;

    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull List<String> content) {
        super(project, file);
        this.project = project;
        this.file = file;
        this.content = content;
    }

    public AppendFileCommandAction(@NotNull Project project, @NotNull PsiFile file, @NotNull final String content) {
        super(project, file);
        this.project = project;
        this.file = file;
        this.content = new ArrayList<String>();
        if (!content.isEmpty()) {
            this.content.add(content);
        }
    }

    @Override
    protected void run(@NotNull Result<PsiFile> result) throws Throwable {
        if (content.isEmpty()) {
            return;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            for (PsiElement element : file.getChildren()) {
                if (content.contains(element.getText())) {
                    Notifications.Bus.notify(new Notification(GitignoreLanguage.NAME,
                            GitignoreBundle.message("action.appendFile.entryExists", element.getText()),
                            "in " + Utils.getRelativePath(project.getBaseDir(), file.getVirtualFile()),
                            NotificationType.WARNING), project);
                    content.remove(element.getText());
                }
            }
            for (String entry : content) {
                document.insertString(document.getTextLength(), "\n" + entry.replace("\r", ""));
            }
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
