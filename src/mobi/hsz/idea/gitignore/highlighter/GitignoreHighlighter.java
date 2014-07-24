package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
<<<<<<< HEAD
import com.intellij.util.containers.ContainerUtil;
=======
import mobi.hsz.idea.gitignore.lang.GitignoreParserDefinition;
>>>>>>> brace-matcher
import mobi.hsz.idea.gitignore.lexer.GitignoreLexerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GitignoreHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = ContainerUtil.newHashMap();

    static {
        fillMap(ATTRIBUTES, GitignoreParserDefinition.COMMENTS, GitignoreHighlighterColors.COMMENT_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.SECTIONS, GitignoreHighlighterColors.SECTION_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.HEADERS, GitignoreHighlighterColors.HEADER_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.NEGATIONS, GitignoreHighlighterColors.NEGATION_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.BRACKETS, GitignoreHighlighterColors.BRACKET_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.SLASHES, GitignoreHighlighterColors.SLASH_ATTR_KEY);
        fillMap(ATTRIBUTES, GitignoreParserDefinition.VALUES, GitignoreHighlighterColors.VALUE_ATTR_KEY);
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
