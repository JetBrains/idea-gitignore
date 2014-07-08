package mobi.hsz.idea.gitignore.ui.tree;

import com.intellij.ui.JBTreeWithHintProvider;

import javax.swing.tree.DefaultTreeModel;

public class GitignoreTree extends JBTreeWithHintProvider {

    public GitignoreTree() {
        setCellRenderer(new GitignoreTreeCellRenderer(this));
    }
}
