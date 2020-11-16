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
        hasFocus: Boolean
    ) {
        if (value !is TemplateTreeNode) {
            return
        }
        val node = value
        val background = if (selected) UIUtil.getTreeSelectionBackground(true) else UIUtil.getTreeBackground()
        UIUtil.changeBackGround(this, background)
        val foreground = when {
            selected -> UIUtil.getTreeSelectionForeground(true)
            node.template == null -> PlatformColors.BLUE
            else -> UIUtil.getTreeForeground()
        }
        val style = SimpleTextAttributes.STYLE_PLAIN
        var text = ""
        var hint = ""
        if (node.template != null) { // template leaf
            text = node.template.name
        } else if (node.container != null) { // container group
            hint = message("template.container." + node.container.toString().toLowerCase())
            checkbox.isVisible = false
        }
        SearchUtil.appendFragments(filter, text, style, foreground, background, textRenderer)
        textRenderer.append(
            hint,
            if (selected) SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, foreground) else SimpleTextAttributes.GRAYED_ATTRIBUTES
        )
        setForeground(foreground)
    }
}
