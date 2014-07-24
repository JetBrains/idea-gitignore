package mobi.hsz.idea.gitignore.psi.impl;

import com.intellij.lang.ASTNode;
import mobi.hsz.idea.gitignore.psi.GitignoreElementImpl;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryFile;
import mobi.hsz.idea.gitignore.psi.GitignoreNegation;

public abstract class GitignoreEntryExtImpl extends GitignoreElementImpl {
    public GitignoreEntryExtImpl(ASTNode node) {
        super(node);
    }

    public boolean isNegated() {
        return getFirstChild() instanceof GitignoreNegation;
    }

    public boolean isDirectory() {
        return this instanceof GitignoreEntryFile;
    }

    public boolean isFile() {
        return !isDirectory();
    }
}
