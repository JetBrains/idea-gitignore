package mobi.hsz.idea.gitignore.ui;

import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.PlatformColors;
import com.intellij.util.ui.UIUtil;
import mobi.hsz.idea.gitignore.GitignoreBundle;

import javax.swing.*;
import java.awt.*;

abstract class TemplateTreeRenderer extends CheckboxTree.CheckboxTreeCellRenderer {
    protected abstract String getFilter();

    public void customizeRenderer(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        if (!(value instanceof TemplateTreeNode)) return;
        TemplateTreeNode node = (TemplateTreeNode) value;

        final Color background = selected ? UIUtil.getTreeSelectionBackground() : UIUtil.getTreeTextBackground();
        UIUtil.changeBackGround(this, background);
        Color foreground = selected ? UIUtil.getTreeSelectionForeground() : node.getTemplate() == null ? PlatformColors.BLUE : UIUtil.getTreeTextForeground();
        int style = SimpleTextAttributes.STYLE_PLAIN;

        String text = "", hint = "";
        if (node.getTemplate() != null) { // template leaf
            text = node.getTemplate().getName();
        } else if (node.getContainer() != null) { // container group
            hint = GitignoreBundle.message("template.container." + node.getContainer().toString().toLowerCase());
            getCheckbox().setVisible(false);
        }

        SearchUtil.appendFragments(getFilter(), text, style, foreground, background, getTextRenderer());
        getTextRenderer().append(hint, selected ? new SimpleTextAttributes(Font.PLAIN, foreground) : SimpleTextAttributes.GRAYED_ATTRIBUTES);
        setForeground(foreground);
    }

}