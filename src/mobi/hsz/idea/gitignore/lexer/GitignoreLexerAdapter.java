package mobi.hsz.idea.gitignore.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public class GitignoreLexerAdapter extends FlexAdapter {
    public GitignoreLexerAdapter(Project project) {
        this(project, null);
    }

    public GitignoreLexerAdapter(Project project, @Nullable VirtualFile virtualFile) {
        super(new GitignoreLexer());
    }
}
