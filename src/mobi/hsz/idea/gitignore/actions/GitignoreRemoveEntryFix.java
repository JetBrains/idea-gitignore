package mobi.hsz.idea.gitignore.actions;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.TreeUtil;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
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
            ASTNode crlf = TreeUtil.findSibling(startElement.getNode(), GitignoreTypes.CRLF);
            if (crlf == null) {
                crlf = TreeUtil.findSiblingBackward(startElement.getNode(), GitignoreTypes.CRLF);
            }
            if (crlf != null) {
                crlf.getPsi().delete();
            }
            startElement.delete();
        }
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return GitignoreBundle.message("codeInspection.group");
    }
}
