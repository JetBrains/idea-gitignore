package mobi.hsz.idea.gitignore.ui.tree;

import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.util.PlatformIcons;
import mobi.hsz.idea.gitignore.util.Utils;

import javax.swing.*;

public class GitignoreTreeObject {
    private final Icon icon;
    private final String name;
    private final boolean directory;
    private final boolean symlink;
    private boolean ignored;
    private final String path;

    public GitignoreTreeObject(PsiFileSystemItem psiFile, VirtualFile rootDirectory) {
        VirtualFile file = psiFile.getVirtualFile();

        directory = file.isDirectory();
        symlink = file.is(VFileProperty.SYMLINK);
        icon = directory ? PlatformIcons.FOLDER_ICON : file.getFileType().getIcon();
        name = file.getName();
        path = Utils.getRelativePath(rootDirectory, file) + (directory ? "/" : "");
    }

    public GitignoreTreeObject(PsiElement element, VirtualFile rootDirectory) {
        this((PsiFileSystemItem) element, rootDirectory);
    }

    public Icon getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public boolean isSymlink() {
        return symlink;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getPath() {
        return path;
    }
}
