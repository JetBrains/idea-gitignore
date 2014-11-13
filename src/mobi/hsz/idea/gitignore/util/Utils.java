/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class Utils {
    public static final double JAVA_VERSION = getJavaVersion();

    private Utils() {
    }

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

    public static List<VirtualFile> getSuitableGitignoreFiles(@NotNull Project project, @NotNull VirtualFile file) throws ExternalFileException {
        List<VirtualFile> files = new ArrayList<VirtualFile>();
        if (file.getCanonicalPath() == null || !VfsUtilCore.isAncestor(project.getBaseDir(), file, true)) {
            throw new ExternalFileException();
        }
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null && !baseDir.equals(file)) {
            do {
                file = file.getParent();
                VirtualFile gitignore = file.findChild(GitignoreLanguage.FILENAME);
                ContainerUtil.addIfNotNull(gitignore, files);
            } while (!file.equals(project.getBaseDir()));
        }
        return files;
    }

    public static boolean isGitDirectory(String path) {
        return path.equals(GitignoreLanguage.GIT_DIRECTORY) || path.startsWith(GitignoreLanguage.GIT_DIRECTORY + VfsUtil.VFS_PATH_SEPARATOR);
    }

    public static List<VirtualFile> getExcludedRoots(@NotNull Project project) {
        List<VirtualFile> roots = new ArrayList<VirtualFile>();
        ModuleManager manager = ModuleManager.getInstance(project);
        for (Module module : manager.getModules()) {
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            Collections.addAll(roots, model.getExcludeRoots());
            model.dispose();
        }
        return roots;
    }

    public static boolean isEntryExcluded(GitignoreEntry entry, Project project) {
        final Pattern pattern = Glob.createPattern(entry.getText());
        if (pattern == null) {
            return false;
        }

        final VirtualFile projectRoot = project.getBaseDir();
        final List<VirtualFile> matched = new ArrayList<VirtualFile>();
        for (final VirtualFile root : getExcludedRoots(project)) {
            VfsUtil.visitChildrenRecursively(root, new VirtualFileVisitor() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    String path = getRelativePath(projectRoot, root);
                    if (path == null) {
                        return false;
                    }
                    if (pattern.matcher(path).matches()) {
                        matched.add(file);
                        return false;
                    }
                    return true;
                }
            });

            if (matched.size() > 0) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getWords(String filter) {
        List<String> words = new ArrayList<String>(Arrays.asList(filter.toLowerCase().split("\\W+")));
        words.removeAll(Arrays.asList(null, ""));
        return words;
    }
}
