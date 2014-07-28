package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.actions.GitignoreRemoveEntryFix;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreVisitor;
import org.jetbrains.annotations.NotNull;

public class GitignoreUnusedEntryInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GitignoreVisitor() {
            @Override
            public void visitEntry(@NotNull GitignoreEntry entry) {
                PsiReference[] references = entry.getReferences();
                boolean resolved = true;
                for (PsiReference reference : references) {
                    if (reference instanceof FileReferenceOwner) {
                        PsiFileReference fileReference = (PsiFileReference) reference;
                        ResolveResult[] result = fileReference.multiResolve(false);
                        resolved = resolved && (result.length > 0 || "*".equals(reference.getCanonicalText()));
                    }
                }

                if (!resolved) {
                    holder.registerProblem(entry, GitignoreBundle.message("codeInspection.unusedEntry.message"), new GitignoreRemoveEntryFix(entry));
                }
                
                super.visitEntry(entry);
            }
        };
    }
}
