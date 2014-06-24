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

    /** Default style for regular entry */
    public static final TextAttributesKey ENTRY_FILE_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.ENTRY_FILE", DefaultLanguageHighlighterColors.STRING);

    /** Default style for directory entry - ends with / */
    public static final TextAttributesKey ENTRY_DIRECTORY_ATTR_KEY = TextAttributesKey.createTextAttributesKey("GITIGNORE.ENTRY_DIRECTORY", DefaultLanguageHighlighterColors.NUMBER);

}
