/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.codeInsight;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeveritiesProvider;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.highlighter.IgnoreHighlighterColors;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Severities provider that checks if entry points to any file or directory.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5.4
 */
public class UnusedEntrySeveritiesProvider extends SeveritiesProvider {
    /** Unused entry {@link HighlightSeverity} instance. */
    @NotNull
    private static final HighlightSeverity UNUSED_ENTRY = new HighlightSeverity("UNUSED ENTRY", 10);

    /**
     * Defines the style of matched entry.
     *
     * @return style definition
     */
    @NotNull
    @Override
    public List<HighlightInfoType> getSeveritiesHighlightInfoTypes() {
        final List<HighlightInfoType> result = new ArrayList<>();

        result.add(new HighlightInfoType.HighlightInfoTypeImpl(
                UNUSED_ENTRY,
                TextAttributesKey.createTextAttributesKey(
                        IgnoreBundle.message("codeInspection.unusedEntry"),
                        IgnoreHighlighterColors.UNUSED
                )
        ));
        return result;
    }

    /**
     * Defines color of the matched entry.
     *
     * @param textAttributes current attribute
     * @return entry color
     */
    @Override
    public Color getTrafficRendererColor(@NotNull TextAttributes textAttributes) {
        return JBColor.GRAY;
    }

    /**
     * Checks if severity goto is enabled.
     *
     * @param minSeverity severity to compare
     * @return severity equals to the {@link #UNUSED_ENTRY}
     */
    @Override
    public boolean isGotoBySeverityEnabled(HighlightSeverity minSeverity) {
        return UNUSED_ENTRY != minSeverity;
    }
}
