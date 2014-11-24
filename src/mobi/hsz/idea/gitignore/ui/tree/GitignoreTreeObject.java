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
