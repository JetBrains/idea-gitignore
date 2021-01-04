// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template

import com.intellij.ide.ui.search.SearchUtil
import com.intellij.ui.CheckboxTree
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.PlatformColors
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import javax.swing.JTree

/**
 * [TemplateTreeRenderer] implementation of checkbox renderer.
 */
abstract class TemplateTreeRenderer : CheckboxTree.CheckboxTreeCellRenderer() {

    protected abstract val filter: String?

    override fun customizeRenderer(
        tree: JTree,
        value: Any,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean,
    ) {
        if (value !is TemplateTreeNode) {
            return
        }
        val foreground = when {
            selected -> UIUtil.getTreeSelectionForeground(true)
            value.template == null -> PlatformColors.BLUE
            else -> UIUtil.getTreeForeground()
        }
        val background = when {
            selected -> UIUtil.getTreeSelectionBackground(true)
            else -> UIUtil.getTreeBackground()
        }

        setForeground(foreground)
        UIUtil.changeBackGround(this, background)

        value.template?.let {
            SearchUtil.appendFragments(filter,
                it.name,
                SimpleTextAttributes.STYLE_PLAIN,
                foreground,
                background,
                textRenderer
            )
        } ?: run {
            value.container?.let {
                checkbox.isVisible = true
                textRenderer.append(
                    message("template.container." + it.toString().toLowerCase()),
                    when {
                        selected -> SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, foreground)
                        else -> SimpleTextAttributes.GRAYED_ATTRIBUTES
                    }
                )
            }
        }

    }
}
