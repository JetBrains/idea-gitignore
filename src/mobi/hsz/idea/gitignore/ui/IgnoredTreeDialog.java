/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

/**
 * {@link IgnoredTreeDialog} that shows a tree with marked ignored files by selected Gitignore file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class IgnoredTreeDialog extends JDialog {
    /** Gitignore file. */
    private final PsiFile file;

    /** Dialog content panel. */
    private JPanel contentPane;

    /** Dialog OK button. */
    private JButton buttonOK;

    /** Dialog Cancel button. */
    private JButton buttonCancel;

    /** Project tree. */
    private JTree tree;

    /**
     * Builds a new instance of {@link IgnoredTreeDialog}.
     *
     * @param file Gitignore file
     */
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

    /** OK button event. */
    private void onOK() {
        // add your code here
        dispose();
    }

    /** Cancel button event. */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /** Dialog show event. */
    public void showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Request for UI components creating. */
    private void createUIComponents() {
        TreeProcessor processor = new TreeProcessor(file);

        DefaultMutableTreeNode root = processor.fetchTree();

        DefaultTreeModel model = new DefaultTreeModel(root);
        tree = new GitignoreTree();
        tree.setModel(model);
    }
}
