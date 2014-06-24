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
    public static final TextAttributesKey NEGATION = createTextAttributesKey("NEGATION", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey ENTRY_FILE = createTextAttributesKey("ENTRY_FILE", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey ENTRY_DIRECTORY = createTextAttributesKey("ENTRY_DIRECTORY", DefaultLanguageHighlighterColors.CLASS_REFERENCE);

    private static final TextAttributesKey[] HEADER_KEYS = new TextAttributesKey[]{HEADER};
    private static final TextAttributesKey[] SECTION_KEYS = new TextAttributesKey[]{SECTION};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] NEGATION_KEYS = new TextAttributesKey[]{NEGATION};
    private static final TextAttributesKey[] ENTRY_FILE_KEYS = new TextAttributesKey[]{ENTRY_FILE};
    private static final TextAttributesKey[] ENTRY_DIRECTORY_KEYS = new TextAttributesKey[]{ENTRY_DIRECTORY};
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
        } else if (tokenType.equals(GitignoreTypes.NEGATION)) {
            return NEGATION_KEYS;
        } else if (tokenType.equals(GitignoreTypes.ENTRY_FILE)) {
            return ENTRY_FILE_KEYS;
        } else if (tokenType.equals(GitignoreTypes.ENTRY_DIRECTORY)) {
            return ENTRY_DIRECTORY_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
