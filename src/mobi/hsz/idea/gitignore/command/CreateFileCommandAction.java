package mobi.hsz.idea.gitignore.command;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.file.GitignoreTemplatesFactory;
import org.jetbrains.annotations.NotNull;

public class CreateFileCommandAction extends WriteCommandAction<PsiFile> {
    private final PsiDirectory directory;

    public CreateFileCommandAction(@NotNull Project project, @NotNull PsiDirectory directory) {
        super(project);
        this.directory = directory;
    }

    @Override
    protected void run(@NotNull Result<PsiFile> result) throws Throwable {
        result.setResult(GitignoreTemplatesFactory.createFromTemplate(directory));
    }
}
