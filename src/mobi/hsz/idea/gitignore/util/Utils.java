package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utils {
    public static final double JAVA_VERSION = getJavaVersion();

    private static double getJavaVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for (; pos < version.length() && count < 2; pos++) {
            if (version.charAt(pos) == '.') count++;
        }
        return Double.parseDouble(version.substring(0, pos - 1));
    }

    @Nullable
    public static String getRelativePath(@NotNull VirtualFile directory, @NotNull VirtualFile file) {
        return VfsUtilCore.getRelativePath(file, directory, '/');
    }

    @Nullable
    public static PsiFile getGitignoreFile(@NotNull Project project) {
        return getGitignoreFile(project, null, false);
    }

    @Nullable
    public static PsiFile getGitignoreFile(@NotNull Project project, @Nullable PsiDirectory directory) {
        return getGitignoreFile(project, directory, false);
    }

    @Nullable
    public static PsiFile getGitignoreFile(@NotNull Project project, @Nullable PsiDirectory directory, boolean createIfMissing) {
        if (directory == null) {
            directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        }

        assert directory != null;
        PsiFile file = directory.findFile(GitignoreLanguage.FILENAME);
        if (file == null && createIfMissing) {
            file = new CreateFileCommandAction(project, directory).execute().getResultObject();
        }

        return file;
    }

    public static void openFile(@NotNull Project project, @NotNull PsiFile file) {
        openFile(project, file.getVirtualFile());
    }

    public static void openFile(@NotNull Project project, @NotNull VirtualFile file) {
        FileEditorManager.getInstance(project).openFile(file, true);
    }

    public static Collection<VirtualFile> getGitignoreFiles(@NotNull Project project) {
        return FilenameIndex.getVirtualFilesByName(project, GitignoreLanguage.FILENAME, GlobalSearchScope.projectScope(project));
    }

    public static List<VirtualFile> getSuitableGitignoreFiles(@NotNull Project project, @NotNull VirtualFile file) {
        List<VirtualFile> files = new ArrayList<VirtualFile>();
        if (!project.getBaseDir().equals(file)) {
            do {
                file = file.getParent();
                VirtualFile gitignore = file.findChild(GitignoreLanguage.FILENAME);
                ContainerUtil.addIfNotNull(gitignore, files);
            } while (!file.equals(project.getBaseDir()));
        }
        return files;
    }
}
