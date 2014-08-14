package mobi.hsz.idea.gitignore.codeInsight;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeveritiesProvider;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.highlighter.GitignoreHighlighterColors;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UnusedEntrySeveritiesProvider extends SeveritiesProvider {
    public static final HighlightSeverity UNUSED_ENTRY = new HighlightSeverity("GITIGNORE.UNUSED_ENTRY", 10);

    @NotNull
    @Override
    public List<HighlightInfoType> getSeveritiesHighlightInfoTypes() {
        final List<HighlightInfoType> result = new ArrayList<HighlightInfoType>();

        final TextAttributes attributes = new TextAttributes();
        attributes.setForegroundColor(JBColor.GRAY);
        attributes.setFontType(Font.ITALIC);

        result.add(new HighlightInfoType.HighlightInfoTypeImpl(
                UNUSED_ENTRY,
                TextAttributesKey.createTextAttributesKey(GitignoreBundle.message("codeInspection.unusedEntry"),
                GitignoreHighlighterColors.UNUSED_ENTRY_ATTR_KEY))
        );
        return result;
    }

    @Override
    public Color getTrafficRendererColor(@NotNull TextAttributes textAttributes) {
        return JBColor.GRAY;
    }

    @Override
    public boolean isGotoBySeverityEnabled(HighlightSeverity minSeverity) {
        return UNUSED_ENTRY != minSeverity;
    }
}
