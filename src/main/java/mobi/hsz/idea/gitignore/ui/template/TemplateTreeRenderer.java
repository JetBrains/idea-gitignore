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
