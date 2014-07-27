package mobi.hsz.idea.gitignore.reference;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

public class GitignoreReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(psiElement().inFile(psiFile(GitignoreFile.class)), new GitignoreReferenceProvider());
    }

    private static class GitignoreReferenceProvider extends PsiReferenceProvider {
        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            if (psiElement instanceof GitignoreEntry) {
                return new GitignoreReferenceSet((GitignoreEntry) psiElement).getAllReferences();
            }
            return PsiReference.EMPTY_ARRAY;
        }
    }
}
