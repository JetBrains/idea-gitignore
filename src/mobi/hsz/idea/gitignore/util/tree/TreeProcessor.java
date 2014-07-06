package mobi.hsz.idea.gitignore.util.tree;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class TreeProcessor {
    private final PsiFile file;

    public TreeProcessor(@NotNull final PsiFile file) {
        this.file = file;

        for (PsiElement element : file.getChildren()) {
            System.out.println(element);
        }
    }
}
