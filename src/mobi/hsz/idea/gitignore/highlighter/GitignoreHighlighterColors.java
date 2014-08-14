package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;


public class GitignoreHighlighterColors {

    /** Default style for regular comment started with # */
    public static final TextAttributesKey COMMENT_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    /** Default style for section comment started with ## */
    public static final TextAttributesKey SECTION_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.SECTION", DefaultLanguageHighlighterColors.DOC_COMMENT);

    /** Default style for header comment started with ### */
    public static final TextAttributesKey HEADER_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.HEADER", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

    /** Default style for negation element - ! in the beginning of the entry */
    public static final TextAttributesKey NEGATION_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.NEGATION", DefaultLanguageHighlighterColors.KEYWORD);

    /** Default style for negation element - ! in the beginning of the entry */
    public static final TextAttributesKey BRACKET_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.BRACKET", DefaultLanguageHighlighterColors.KEYWORD);

    /** Default style for negation element - ! in the beginning of the entry */
    public static final TextAttributesKey SLASH_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.SLASH", DefaultLanguageHighlighterColors.COMMA);

    /** Default style for negation element - ! in the beginning of the entry */
    public static final TextAttributesKey VALUE_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.VALUE", DefaultLanguageHighlighterColors.STRING);

    /** Default style for unused entry */
    public static final TextAttributesKey UNUSED_ENTRY_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.UNUSED_ENTRY", DefaultLanguageHighlighterColors.DOC_COMMENT);


}
