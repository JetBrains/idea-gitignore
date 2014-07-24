package mobi.hsz.idea.gitignore.actions;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import org.jetbrains.annotations.NotNull;

public class GitignoreRemoveEntryFix extends LocalQuickFixOnPsiElement {
    public GitignoreRemoveEntryFix(@NotNull GitignoreEntry entry) {
        super(entry);
    }

    @NotNull
    @Override
    public String getText() {
        return GitignoreBundle.message("quick.fix.remove.entry");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (startElement instanceof GitignoreEntry) {
            startElement.delete();
        }
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return GitignoreBundle.message("codeInspection.group");
    }
}
