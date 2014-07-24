package mobi.hsz.idea.gitignore.daemon;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryDirectory;
import mobi.hsz.idea.gitignore.psi.GitignoreEntryFile;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GitignoreLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        boolean isDirectory = element instanceof GitignoreEntryDirectory;
        if (!isDirectory && element instanceof GitignoreEntryFile) {
            GitignoreEntryFile entry = (GitignoreEntryFile) element;
            String path = entry.getText();
            if (entry.getNegation() != null) {
                path = path.substring(1);
            }
            VirtualFile parent = element.getContainingFile().getVirtualFile().getParent();
            if (parent != null) {
                List<VirtualFile> files = Glob.find(parent, path);
                for (VirtualFile file : files) {
                    if (!file.isDirectory()) {
                        return;
                    }
                }
                isDirectory = files.size() > 0;
            }
        }

        if (isDirectory) {
            RelatedItemLineMarkerInfo<PsiElement> marker = NavigationGutterIconBuilder
                    .create(PlatformIcons.FOLDER_ICON)
                    .setTargets(element)
                    .setTooltipText(GitignoreBundle.message("daemon.lineMarker.directory"))
                    .createLineMarkerInfo(element);
            result.add(marker);
        }
    }
}
