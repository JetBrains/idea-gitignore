/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.util.tree;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryDirectory;
import mobi.hsz.idea.gitignore.ui.tree.GitignoreTreeObject;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TreeProcessor {
    private final PsiFile file;
    private final ArrayList<Rule> rules = new ArrayList<Rule>();
    private final VirtualFile rootDirectory;

    public TreeProcessor(@NotNull final PsiFile file) {
        this.file = file;
        this.rootDirectory = file.getVirtualFile() != null ? file.getVirtualFile().getParent() : file.getProject().getBaseDir();

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
        private final Pattern pattern;

        public Rule(GitignoreEntry entry) {
            negated = entry.getNegation() != null;
            directory = entry instanceof GitignoreEntryDirectory;
            value = entry.getText();
            pattern = Glob.createPattern(value);
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

        public Pattern getPattern() {
            return pattern;
        }

        public boolean match(String path) {
            return pattern != null && pattern.matcher(path).matches();
        }
    }
}
