package mobi.hsz.idea.gitignore.ui.tree;

import com.intellij.ui.JBColor;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class GitignoreTreeCellRenderer extends JBDefaultTreeCellRenderer {
    public GitignoreTreeCellRenderer(@NotNull JTree tree) {
        super(tree);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        GitignoreTreeCellRenderer component = (GitignoreTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

        if (userObject instanceof GitignoreTreeObject) {
            GitignoreTreeObject object = (GitignoreTreeObject) userObject;
            component.setIcon(object.getIcon());

            if (object.isIgnored()) {
                component.setForeground(JBColor.GRAY);
            }
        }

        return component;
    }
}
