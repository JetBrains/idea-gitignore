package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GitignoreCompletionContributor extends CompletionContributor {
    public GitignoreCompletionContributor() {
        CompletionProvider<CompletionParameters> provider = new PathCompletionProvider();

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(GitignoreTypes.ENTRY_FILE)
                .withLanguage(GitignoreLanguage.INSTANCE), provider);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(GitignoreTypes.ENTRY_DIRECTORY)
                .withLanguage(GitignoreLanguage.INSTANCE), provider);
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);
    }

    @Nullable
    @Override
    public String handleEmptyLookup(@NotNull CompletionParameters parameters, Editor editor) {
        return super.handleEmptyLookup(parameters, editor);
    }

    @Nullable
    @Override
    public AutoCompletionDecision handleAutoCompletionPossibility(AutoCompletionContext context) {
        return super.handleAutoCompletionPossibility(context);
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return super.invokeAutoPopup(position, typeChar);
    }

    @Override
    public void duringCompletion(@NotNull CompletionInitializationContext context) {
        super.duringCompletion(context);
    }
}
