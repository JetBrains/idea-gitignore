package mobi.hsz.idea.gitignore.psi;

import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.lang.GitignoreLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GitignoreElementType extends IElementType {
    public GitignoreElementType(@NotNull @NonNls String debugName) {
        super(debugName, GitignoreLanguage.INSTANCE);
    }
}
