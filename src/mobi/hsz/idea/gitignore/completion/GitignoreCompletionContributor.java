package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;

public class GitignoreCompletionContributor extends CompletionContributor {
    public GitignoreCompletionContributor() {
        CompletionProvider<CompletionParameters> provider = new PathCompletionProvider();

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(GitignoreTypes.ENTRY_FILE)
                .withLanguage(GitignoreLanguage.INSTANCE), provider);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(GitignoreTypes.ENTRY_DIRECTORY)
                .withLanguage(GitignoreLanguage.INSTANCE), provider);
    }
}
