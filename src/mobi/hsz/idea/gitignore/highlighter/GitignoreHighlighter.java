package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.lexer.GitignoreLexerAdapter;
import mobi.hsz.idea.gitignore.psi.GitignoreTokenTypeSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GitignoreHighlighter extends SyntaxHighlighterBase {

    protected static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.COMMENT_SET, GitignoreHighlighterColors.COMMENT_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.SECTION_SET, GitignoreHighlighterColors.SECTION_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.HEADER_SET, GitignoreHighlighterColors.HEADER_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.NEGATION_SET, GitignoreHighlighterColors.NEGATION_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.ENTRY_FILE_SET, GitignoreHighlighterColors.ENTRY_FILE_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreTokenTypeSets.ENTRY_DIRECTORY_SET, GitignoreHighlighterColors.ENTRY_DIRECTORY_ATTR_KEY);
    }

    private final Project project;
    private final VirtualFile virtualFile;

    public GitignoreHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        this.project = project;
        this.virtualFile = virtualFile;
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new GitignoreLexerAdapter(project, virtualFile);
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
