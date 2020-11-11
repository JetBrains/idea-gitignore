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
        @NonNls private val SAMPLE_GITIGNORE_PATH = "/sample.gitignore"
        @NonNls private val DISPLAY_NAME = IgnoreBundle.message("ignore.colorSettings.displayName")
        private val SAMPLE_GITIGNORE = loadSampleGitignore()
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

    override fun getIcon(): Icon? = null

    override fun getHighlighter() = IgnoreHighlighter(null)

    override fun getDemoText() = SAMPLE_GITIGNORE

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors() = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = DISPLAY_NAME
}
