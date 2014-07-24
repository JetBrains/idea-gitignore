package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Resources;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class GitignoreColorSettingsPage implements ColorSettingsPage {

    /** The path to the sample .gitignore file */
    @NonNls
    private static final String SAMPLE_GITIGNORE_PATH = "/sample.gitignore";

    /**
     * The sample .gitignore document shown in the colors settings dialog
     *
     * @see #loadSampleGitignore()
     */
    private static final String SAMPLE_GITIGNORE = loadSampleGitignore();

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor(GitignoreBundle.message("highlighter.header"), GitignoreHighlighterColors.HEADER_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.section"), GitignoreHighlighterColors.SECTION_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.comment"), GitignoreHighlighterColors.COMMENT_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.negation"), GitignoreHighlighterColors.NEGATION_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.brackets"), GitignoreHighlighterColors.BRACKET_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.slash"), GitignoreHighlighterColors.SLASH_ATTR_KEY),
            new AttributesDescriptor(GitignoreBundle.message("highlighter.value"), GitignoreHighlighterColors.VALUE_ATTR_KEY),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new GitignoreHighlighter(null, null);
    }

    @NotNull
    @Override
    public String getDemoText() {
        return SAMPLE_GITIGNORE;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return GitignoreLanguage.NAME;
    }

    /**
     * Loads sample .gitignore file
     *
     * @return the text loaded from {@link #SAMPLE_GITIGNORE_PATH}
     * @see #getDemoText()
     * @see #SAMPLE_GITIGNORE_PATH
     * @see #SAMPLE_GITIGNORE
     */
    private static String loadSampleGitignore() {
        return Resources.getResourceContent(SAMPLE_GITIGNORE_PATH);
    }
}
