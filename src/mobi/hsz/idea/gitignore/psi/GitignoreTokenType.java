package mobi.hsz.idea.gitignore.psi;

import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GitignoreTokenType extends IElementType {
    private final String debugName;

    public GitignoreTokenType(@NotNull @NonNls String debugName) {
        super(debugName, GitignoreLanguage.INSTANCE);
        this.debugName = debugName;
    }

    @Override
    public String toString() {
        return GitignoreBundle.messageOrDefault("tokenType." + debugName, GitignoreLanguage.NAME + "TokenType." + super.toString());
    }
}
