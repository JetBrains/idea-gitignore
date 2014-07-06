package mobi.hsz.idea.gitignore.ui.tree;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import com.intellij.util.PlatformIcons;
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
        Object object = ((DefaultMutableTreeNode) value).getUserObject();
        if (object instanceof VirtualFile) {
            VirtualFile file = (VirtualFile) object;
            component.setIcon(file.isDirectory() ? PlatformIcons.FOLDER_ICON : file.getFileType().getIcon());
        }

        return component;
    }
}
