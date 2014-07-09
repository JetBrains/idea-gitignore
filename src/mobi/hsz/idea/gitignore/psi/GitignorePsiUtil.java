package mobi.hsz.idea.gitignore.psi;

import com.intellij.psi.PsiElement;
import mobi.hsz.idea.gitignore.psi.impl.GitignoreEntryImpl;

public class GitignorePsiUtil {

    public static PsiElement getEntry(GitignoreEntryImpl entry) {
        return (entry.getEntryDirectory() != null) ? entry.getEntryDirectory() : entry.getEntryFile();
    }

}
