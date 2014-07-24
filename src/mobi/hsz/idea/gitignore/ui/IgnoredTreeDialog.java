package mobi.hsz.idea.gitignore.ui;

import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.ui.tree.GitignoreTree;
import mobi.hsz.idea.gitignore.util.Utils;
import mobi.hsz.idea.gitignore.util.tree.TreeProcessor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;

public class IgnoredTreeDialog extends JDialog {
    private final PsiFile file;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTree tree;

    public IgnoredTreeDialog(@NotNull final PsiFile file) {
        this.file = file;
        String filePath = Utils.getRelativePath(file.getProject().getBaseDir(), file.getVirtualFile());

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(GitignoreBundle.message("dialog.ignoredTree.title", filePath));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createUIComponents() {
        TreeProcessor processor = new TreeProcessor(file);

        DefaultMutableTreeNode root = processor.fetchTree();

        DefaultTreeModel model = new DefaultTreeModel(root);
        tree = new GitignoreTree();
        tree.setModel(model);
    }

}
