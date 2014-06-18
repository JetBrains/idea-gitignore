package mobi.hsz.idea.gitignore;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import mobi.hsz.idea.gitignore.util.GitignoreIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class GitignoreColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Header", GitignoreSyntaxHighlighter.HEADER),
            new AttributesDescriptor("Section", GitignoreSyntaxHighlighter.SECTION),
            new AttributesDescriptor("Comment", GitignoreSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("File", GitignoreSyntaxHighlighter.ENTRY_FILE),
            new AttributesDescriptor("Directory", GitignoreSyntaxHighlighter.ENTRY_DIRECTORY),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return GitignoreIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new GitignoreSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "foo";
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
        return "Gitignore";
    }
}
