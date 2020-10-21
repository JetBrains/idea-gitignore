// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInsight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType.HighlightInfoTypeImpl
import com.intellij.codeInsight.daemon.impl.SeveritiesProvider
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.highlighter.IgnoreHighlighterColors
import java.awt.Color

/**
 * Severities provider that checks if entry points to any file or directory.
 */
class UnusedEntrySeveritiesProvider : SeveritiesProvider() {

    companion object {
        /** Unused entry [HighlightSeverity] instance.  */
        private val UNUSED_ENTRY = HighlightSeverity("UNUSED ENTRY", 10)
    }

    /**
     * Defines the style of matched entry.
     *
     * @return style definition
     */
    override fun getSeveritiesHighlightInfoTypes() = listOf(
        HighlightInfoTypeImpl(
            UNUSED_ENTRY,
            TextAttributesKey.createTextAttributesKey(IgnoreBundle.message("codeInspection.unusedEntry"), IgnoreHighlighterColors.UNUSED)
        )
    )

    /**
     * Defines color of the matched entry.
     *
     * @param textAttributes current attribute
     * @return entry color
     */
    override fun getTrafficRendererColor(textAttributes: TextAttributes): Color = JBColor.GRAY

    /**
     * Checks if severity goto is enabled.
     *
     * @param minSeverity severity to compare
     * @return severity equals to the [.UNUSED_ENTRY]
     */
    override fun isGotoBySeverityEnabled(minSeverity: HighlightSeverity) = UNUSED_ENTRY !== minSeverity
}
