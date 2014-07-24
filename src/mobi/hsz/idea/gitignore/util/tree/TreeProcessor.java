package mobi.hsz.idea.gitignore.util.tree;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreNegatedEntry;
import mobi.hsz.idea.gitignore.ui.tree.GitignoreTreeObject;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class TreeProcessor {
    private final PsiFile file;
    private final ArrayList<Rule> rules = new ArrayList<Rule>();
    private final VirtualFile rootDirectory;

    public TreeProcessor(@NotNull final PsiFile file) {
        this.file = file;
        this.rootDirectory = file.getVirtualFile() != null ? file.getVirtualFile() : file.getProject().getBaseDir();

        for (PsiElement element : file.getChildren()) {
            if (element instanceof GitignoreEntry) {
                rules.add(new Rule((GitignoreEntry) element));
            }
        }
    }

    public DefaultMutableTreeNode fetchTree() {
        GitignoreTreeObject node = new GitignoreTreeObject(file, rootDirectory);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(node);
        buildTree(root, file.getParent(), false);
        return root;
    }

    private void buildTree(DefaultMutableTreeNode root, PsiFileSystemItem file, boolean parentIgnored) {
        for (PsiElement element : file.getChildren()) {
            GitignoreTreeObject object = new GitignoreTreeObject(element, rootDirectory);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(object);
            boolean ignored = parentIgnored;

            if (!parentIgnored) {
                for (Rule rule : rules) {
                    if (rule.match(object.getPath())) {
                        ignored = !rule.isNegated();
                    }
                }
            }
            object.setIgnored(ignored);

            if (object.isDirectory() && !object.isSymlink()) {
                buildTree(node, (PsiFileSystemItem) element, ignored);
            }

            root.add(node);
        }
    }

    private static class Rule {
        private final boolean negated;
        private final boolean directory;
        private final String value;
        private final String regex;

        public Rule(GitignoreEntry entry) {
            negated = (entry instanceof GitignoreNegatedEntry);
            directory = entry.getEntryDirectory() != null;
            value = entry.getEntry().getText();
            regex = Utils.createRegexFromGlob(value);
        }

        public boolean isNegated() {
            return negated;
        }

        public boolean isDirectory() {
            return directory;
        }

        public String getValue() {
            return value;
        }

        public String getRegex() {
            return regex;
        }

        public boolean match(String path) {
            return path.matches(regex);
        }
    }
}
