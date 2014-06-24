package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.GitignoreLexerAdapter;
import mobi.hsz.idea.gitignore.lang.GitignoreTokenTypeSets;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new GitignoreLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
