// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.highlighter

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.util.Resources
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * [ColorSettingsPage] that allows to modify color scheme.
 */
class IgnoreColorSettingsPage : ColorSettingsPage {

    companion object {
        /** The path to the sample .gitignore file.  */
        @NonNls
        private val SAMPLE_GITIGNORE_PATH = "/sample.gitignore"

        /** Display name for Color Settings Page.  */
        @NonNls
        private val DISPLAY_NAME = IgnoreBundle.message("ignore.colorSettings.displayName")

        /**
         * The sample .gitignore document shown in the colors settings dialog.
         *
         * @see .loadSampleGitignore
         */
        private val SAMPLE_GITIGNORE = loadSampleGitignore()

        /** Attributes descriptor list.  */
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor(IgnoreBundle.message("highlighter.header"), IgnoreHighlighterColors.HEADER),
            AttributesDescriptor(IgnoreBundle.message("highlighter.section"), IgnoreHighlighterColors.SECTION),
            AttributesDescriptor(IgnoreBundle.message("highlighter.comment"), IgnoreHighlighterColors.COMMENT),
            AttributesDescriptor(IgnoreBundle.message("highlighter.negation"), IgnoreHighlighterColors.NEGATION),
            AttributesDescriptor(IgnoreBundle.message("highlighter.brackets"), IgnoreHighlighterColors.BRACKET),
            AttributesDescriptor(IgnoreBundle.message("highlighter.slash"), IgnoreHighlighterColors.SLASH),
            AttributesDescriptor(IgnoreBundle.message("highlighter.syntax"), IgnoreHighlighterColors.SYNTAX),
            AttributesDescriptor(IgnoreBundle.message("highlighter.value"), IgnoreHighlighterColors.VALUE),
            AttributesDescriptor(IgnoreBundle.message("highlighter.unused"), IgnoreHighlighterColors.UNUSED)
        )

        /**
         * Loads sample .gitignore file
         *
         * @return the text loaded from [.SAMPLE_GITIGNORE_PATH]
         *
         * @see .getDemoText
         * @see .SAMPLE_GITIGNORE_PATH
         * @see .SAMPLE_GITIGNORE
         */
        private fun loadSampleGitignore() = Resources.getResourceContent(SAMPLE_GITIGNORE_PATH)!!
    }

    /**
     * Returns the icon for the page, shown in the dialog tab.
     *
     * @return the icon for the page, or null if the page does not have a custom icon.
     */
    override fun getIcon(): Icon? = null

    /**
     * Returns the syntax highlighter which is used to highlight the text shown in the preview
     * pane of the page.
     *
     * @return the syntax highlighter instance.
     */
    override fun getHighlighter() = IgnoreHighlighter(null, null)

    /**
     * Returns the text shown in the preview pane.
     *
     * @return demo text
     */
    override fun getDemoText() = SAMPLE_GITIGNORE

    /**
     * Returns the mapping from special tag names surrounding the regions to be highlighted
     * in the preview text.
     *
     * @return `null`
     */
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    /**
     * Returns the list of descriptors specifying the [TextAttributesKey] instances
     * for which colors are specified in the page. For such attribute keys, the user can choose
     * all highlighting attributes (font type, background color, foreground color, error stripe color and
     * effects).
     *
     * @return the list of attribute descriptors.
     */
    override fun getAttributeDescriptors() = DESCRIPTORS

    /**
     * Returns the list of descriptors specifying the [com.intellij.openapi.editor.colors.ColorKey]
     * instances for which colors are specified in the page. For such color keys, the user can
     * choose only the background or foreground color.
     *
     * @return the list of color descriptors.
     */
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    /**
     * Returns the title of the page, shown as text in the dialog tab.
     *
     * @return the title of the custom page.
     */
    override fun getDisplayName(): String = DISPLAY_NAME
}
