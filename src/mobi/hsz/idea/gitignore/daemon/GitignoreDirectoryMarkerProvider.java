package mobi.hsz.idea.gitignore.daemon;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryDirectory;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryFile;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GitignoreDirectoryMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        boolean isDirectory = element instanceof GitignoreEntryDirectory;
        if (!isDirectory && element instanceof GitignoreEntryFile) {
            GitignoreEntryFile entry = (GitignoreEntryFile) element;
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

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }
}
