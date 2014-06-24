package mobi.hsz.idea.gitignore.psi;

import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GitignoreTokenType extends IElementType {
    public GitignoreTokenType(@NotNull @NonNls String debugName) {
        super(debugName, GitignoreLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return GitignoreLanguage.NAME + "TokenType." + super.toString();
    }
}
