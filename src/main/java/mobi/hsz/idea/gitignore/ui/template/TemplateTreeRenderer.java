// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template;

import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.PlatformColors;
import com.intellij.util.ui.UIUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;

import javax.swing.*;
import java.awt.*;

/**
 * {@link TemplateTreeRenderer} implementation of checkbox renderer.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6
 */
public abstract class TemplateTreeRenderer extends CheckboxTree.CheckboxTreeCellRenderer {
    /** Returns current filter. */
    protected abstract String getFilter();

    /**
     * Renders checkbox tree cell filled with @{link TemplateTreeNode} data.
     *
     * @param tree     current working tree
     * @param value    template data
     * @param selected node is selected
     * @param expanded node is expanded
     * @param leaf     node is a leaf
     * @param row      node is a row
     * @param hasFocus node has focus
     */
    public void customizeRenderer(final JTree tree, final Object value, final boolean selected, final boolean expanded,
                                  final boolean leaf, final int row, final boolean hasFocus) {
        if (!(value instanceof TemplateTreeNode)) {
            return;
        }
        TemplateTreeNode node = (TemplateTreeNode) value;

        final Color background = selected ? UIUtil.getTreeSelectionBackground(true) : UIUtil.getTreeBackground();
        UIUtil.changeBackGround(this, background);
        Color foreground = selected ? UIUtil.getTreeSelectionForeground(true) : node.getTemplate() == null ?
                PlatformColors.BLUE : UIUtil.getTreeForeground();
        int style = SimpleTextAttributes.STYLE_PLAIN;

        String text = "", hint = "";
        if (node.getTemplate() != null) { // template leaf
            text = node.getTemplate().getName();
        } else if (node.getContainer() != null) { // container group
            hint = IgnoreBundle.message("template.container." + node.getContainer().toString().toLowerCase());
            getCheckbox().setVisible(false);
        }

        SearchUtil.appendFragments(getFilter(), text, style, foreground, background, getTextRenderer());
        getTextRenderer().append(hint, selected
                ? new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, foreground)
                : SimpleTextAttributes.GRAYED_ATTRIBUTES
        );
        setForeground(foreground);
    }
}
