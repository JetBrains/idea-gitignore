package mobi.hsz.idea.gitignore.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import mobi.hsz.idea.gitignore.lang.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.GitignoreIcons;
import mobi.hsz.idea.gitignore.util.Resources;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class GitignoreColorSettingsPage implements ColorSettingsPage {

    /** The path to the sample .gitignore file */
    @NonNls
    protected static final String SAMPLE_GITIGNORE_PATH = "/sample.gitignore";

    /**
     * The sample .gitignore document shown in the colors settings dialog
     *
     * @see #loadSampleGitignore()
     */
    protected static final String SAMPLE_GITIGNORE = loadSampleGitignore();

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Header", GitignoreHighlighterColors.HEADER_ATTR_KEY),
            new AttributesDescriptor("Section", GitignoreHighlighterColors.SECTION_ATTR_KEY),
            new AttributesDescriptor("Comment", GitignoreHighlighterColors.COMMENT_ATTR_KEY),
            new AttributesDescriptor("Negation", GitignoreHighlighterColors.NEGATION_ATTR_KEY),
            new AttributesDescriptor("File", GitignoreHighlighterColors.ENTRY_FILE_ATTR_KEY),
            new AttributesDescriptor("Directory", GitignoreHighlighterColors.ENTRY_DIRECTORY_ATTR_KEY),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return GitignoreIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new GitignoreHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return loadSampleGitignore();
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
    protected static String loadSampleGitignore() {
        return Resources.getTemplate(SAMPLE_GITIGNORE_PATH);
    }
}
