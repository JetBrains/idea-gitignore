package mobi.hsz.idea.gitignore;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class GitignoreSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey HEADER = createTextAttributesKey("HEADER", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey SECTION = createTextAttributesKey("SECTION", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TextAttributesKey[] HEADER_KEYS = new TextAttributesKey[]{HEADER};
    private static final TextAttributesKey[] SECTION_KEYS = new TextAttributesKey[]{SECTION};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new GitignoreLexer((Reader) null));
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(GitignoreTypes.HEADER)) {
            return HEADER_KEYS;
        } else if (tokenType.equals(GitignoreTypes.SECTION)) {
            return SECTION_KEYS;
        } else if (tokenType.equals(GitignoreTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
