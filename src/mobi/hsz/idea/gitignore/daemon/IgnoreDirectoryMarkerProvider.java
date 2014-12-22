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

package mobi.hsz.idea.gitignore.daemon;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import mobi.hsz.idea.gitignore.psi.IgnoreEntryDirectory;
import mobi.hsz.idea.gitignore.psi.IgnoreEntryFile;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * {@link LineMarkerProvider} that marks entry lines with directory icon if they point to the directory in virtual system.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreDirectoryMarkerProvider implements LineMarkerProvider {
    /**
     * Returns {@link LineMarkerInfo} with set {@link PlatformIcons#FOLDER_ICON} if entry points to the directory.
     *
     * @param element current element
     * @return <code>null</code> if entry is not a directory
     */
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        boolean isDirectory = element instanceof IgnoreEntryDirectory;
        if (!isDirectory && element instanceof IgnoreEntryFile) {
            IgnoreEntryFile entry = (IgnoreEntryFile) element;
            String path = entry.getText();
            if (entry.getNegation() != null) {
                path = path.substring(1);
            }
            VirtualFile parent = element.getContainingFile().getVirtualFile().getParent();
            List<VirtualFile> files = Glob.find(parent, path);
            for (VirtualFile file : files) {
                if (!file.isDirectory()) {
                    return null;
                }
            }
            isDirectory = files.size() > 0;
        }

        if (isDirectory) {
            return new LineMarkerInfo<PsiElement>(element, element.getTextRange().getStartOffset(),
                    PlatformIcons.FOLDER_ICON, Pass.UPDATE_ALL, null, null);
        }
        return null;
    }

    /**
     * Mocked method.
     *
     * @param elements unused parameter
     * @param result   unused parameter
     */
    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }
}
