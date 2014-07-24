package mobi.hsz.idea.gitignore.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import org.jetbrains.annotations.NotNull;

public class GitignoreEntryManipulator extends AbstractElementManipulator<GitignoreEntry> {
    @Override
    public GitignoreEntry handleContentChange(@NotNull GitignoreEntry entry, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        GitignoreFile file = (GitignoreFile) PsiFileFactory.getInstance(entry.getProject())
                .createFileFromText(GitignoreLanguage.FILENAME, GitignoreFileType.INSTANCE, textRange.replace(entry.getText(), s));
        GitignoreEntry newEntry = PsiTreeUtil.findChildOfType(file, GitignoreEntry.class);
        assert newEntry != null;
        return (GitignoreEntry) entry.replace(newEntry);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull GitignoreEntry element) {
        GitignoreNegation negation = element.getNegation();
        if (negation != null) {
            return TextRange.create(negation.getStartOffsetInParent() + negation.getTextLength(), element.getTextLength());
        }
        return super.getRangeInElement(element);
    }
}
