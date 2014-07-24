package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return VfsUtilCore.getRelativePath(directory, file, '/');
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

    public static String createRegexFromGlob(String glob) {
        glob = glob.trim();
        int strLen = glob.length();
        StringBuilder sb = new StringBuilder(strLen);
        boolean limit = false;

        // Remove beginning and ending * globs because they're useless
        if (StringUtil.startsWithChar(glob, '/')) {
            sb.append("^");
            glob = glob.substring(1);
            strLen--;
        } else {
            if (StringUtil.startsWithChar(glob, '*')) {
                glob = glob.substring(1);
                strLen--;
            }
            sb.append(".*?");
        }
        if (StringUtil.endsWithChar(glob, '*')) {
            glob = glob.substring(0, strLen - 1);
        } else {
            limit = true;
        }
        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : glob.toCharArray()) {
            switch (currentChar) {
                case '*':
                    if (escaping)
                        sb.append("\\*");
                    else
                        sb.append(".*");
                    escaping = false;
                    break;
                case '?':
                    if (escaping)
                        sb.append("\\?");
                    else
                        sb.append('.');
                    escaping = false;
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    sb.append('\\');
                    sb.append(currentChar);
                    escaping = false;
                    break;
                case '\\':
                    if (escaping) {
                        sb.append("\\\\");
                        escaping = false;
                    } else
                        escaping = true;
                    break;
                case '{':
                    if (escaping) {
                        sb.append("\\{");
                    } else {
                        sb.append('(');
                        inCurlies++;
                    }
                    escaping = false;
                    break;
                case '}':
                    if (inCurlies > 0 && !escaping) {
                        sb.append(')');
                        inCurlies--;
                    } else if (escaping)
                        sb.append("\\}");
                    else
                        sb.append("}");
                    escaping = false;
                    break;
                case ',':
                    if (inCurlies > 0 && !escaping) {
                        sb.append('|');
                    } else if (escaping)
                        sb.append("\\,");
                    else
                        sb.append(",");
                    break;
                default:
                    escaping = false;
                    sb.append(currentChar);
            }
        }

        if (limit) {
            sb.append("$");
        } else {
            sb.append(".");
            sb.append(StringUtil.endsWithChar(glob, '/') ? "+" : "*");
        }

        return sb.toString();
    }
}
