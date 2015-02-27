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

package mobi.hsz.idea.gitignore.util;

import com.google.common.collect.Sets;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
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
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link Utils} class that contains various methods.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3.3
 */
public class Utils {
    /** Gitignore plugin ID */
    @NonNls
    private static final String PLUGIN_ID = "mobi.hsz.idea.gitignore";

    /** Private constructor to prevent creating {@link Utils} instance. */
    private Utils() {
    }

    /**
     * Gets relative path of given @{link VirtualFile} and root directory.
     *
     * @param directory root directory
     * @param file      file to get it's path
     * @return relative path
     */
    @Nullable
    public static String getRelativePath(@NotNull VirtualFile directory, @NotNull VirtualFile file) {
        return VfsUtilCore.getRelativePath(file, directory, '/');
    }

    /**
     * Gets Ignore file for given {@link Project} root directory.
     *
     * @param project  current project
     * @param fileType current ignore file type
     * @return Ignore file
     */
    @Nullable
    public static PsiFile getIgnoreFile(@NotNull Project project, @NotNull IgnoreFileType fileType) {
        return getIgnoreFile(project, fileType, null, false);
    }

    /**
     * Gets Ignore file for given {@link Project} and root {@link PsiDirectory}.
     *
     * @param project   current project
     * @param fileType  current ignore file type
     * @param directory root directory
     * @return Ignore file
     */
    @Nullable
    public static PsiFile getIgnoreFile(@NotNull Project project, @NotNull IgnoreFileType fileType, @Nullable PsiDirectory directory) {
        return getIgnoreFile(project, fileType, directory, false);
    }

    /**
     * Gets Ignore file for given {@link Project} and root {@link PsiDirectory}.
     * If file is missing - creates new one.
     *
     * @param project         current project
     * @param fileType        current ignore file type
     * @param directory       root directory
     * @param createIfMissing create new file if missing
     * @return Ignore file
     */
    @Nullable
    public static PsiFile getIgnoreFile(@NotNull Project project, @NotNull IgnoreFileType fileType, @Nullable PsiDirectory directory, boolean createIfMissing) {
        if (directory == null) {
            directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        }

        assert directory != null;
        PsiFile file = directory.findFile(fileType.getIgnoreLanguage().getFilename());
        if (file == null && createIfMissing) {
            file = new CreateFileCommandAction(project, directory, fileType).execute().getResultObject();
        }

        return file;
    }

    /**
     * Opens given file in editor.
     *
     * @param project current project
     * @param file    file to open
     */
    public static void openFile(@NotNull Project project, @NotNull PsiFile file) {
        openFile(project, file.getVirtualFile());
    }

    /**
     * Opens given file in editor.
     *
     * @param project current project
     * @param file    file to open
     */
    public static void openFile(@NotNull Project project, @NotNull VirtualFile file) {
        FileEditorManager.getInstance(project).openFile(file, true);
    }

    /**
     * Returns all Ignore files in given {@link Project} that can match current passed file.
     *
     * @param project current project
     * @param file    current file
     * @return collection of suitable Ignore files
     * @throws ExternalFileException
     */
    public static List<VirtualFile> getSuitableIgnoreFiles(@NotNull Project project, @NotNull IgnoreFileType fileType, @NotNull VirtualFile file)
            throws ExternalFileException {
        List<VirtualFile> files = new ArrayList<VirtualFile>();
        if (file.getCanonicalPath() == null || !VfsUtilCore.isAncestor(project.getBaseDir(), file, true)) {
            throw new ExternalFileException();
        }
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null && !baseDir.equals(file)) {
            do {
                file = file.getParent();
                VirtualFile ignoreFile = file.findChild(fileType.getIgnoreLanguage().getFilename());
                ContainerUtil.addIfNotNull(ignoreFile, files);
            } while (!file.equals(project.getBaseDir()));
        }
        return files;
    }

    /**
     * Checks if given path is a {@link mobi.hsz.idea.gitignore.lang.kind.GitLanguage#getGitDirectory()}.
     *
     * @param path to check
     * @return given path is <code>.git</code> directory
     */
    public static boolean isGitDirectory(String path) {
        final String directory = GitLanguage.INSTANCE.getGitDirectory();
        return path.equals(directory) || path.startsWith(directory + VfsUtil.VFS_PATH_SEPARATOR);
    }

    /**
     * Searches for excluded roots in given {@link Project}.
     *
     * @param project current project
     * @return list of excluded roots
     */
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

    /**
     * Checks if given {@link IgnoreEntry} is excluded in the current {@link Project}.
     *
     * @param entry   Gitignore entry
     * @param project current project
     * @return entry is excluded in current project
     */
    public static boolean isEntryExcluded(IgnoreEntry entry, Project project) {
        final Pattern pattern = Glob.createPattern(entry);
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

    /**
     * Gets list of words for given {@link String} excluding special characters.
     *
     * @param filter input string
     * @return list of words without special characters
     */
    public static List<String> getWords(String filter) {
        List<String> words = new ArrayList<String>(Arrays.asList(filter.toLowerCase().split("\\W+")));
        words.removeAll(Arrays.asList(null, ""));
        return words;
    }

    /**
     * Returns Gitignore plugin information.
     *
     * @return {@link IdeaPluginDescriptor}
     */
    public static IdeaPluginDescriptor getPlugin() {
        return PluginManager.getPlugin(PluginId.getId(PLUGIN_ID));
    }

    /**
     * Checks if lists are equal.
     *
     * @param l1 first list
     * @param l2 second list
     * @return lists are equal
     */
    public static boolean equalLists(@NotNull List<?> l1, @NotNull List<?> l2) {
        return l1.size() == l2.size() && l1.containsAll(l2) && l2.containsAll(l1);
    }

    /**
     * Returns {@link IgnoreFileType} basing on the {@link VirtualFile} file.
     *
     * @param virtualFile current file
     * @return file type
     */
    public static IgnoreFileType getFileType(@Nullable VirtualFile virtualFile) {
        if (virtualFile != null) {
            FileType fileType = virtualFile.getFileType();
            if (fileType instanceof IgnoreFileType) {
                return (IgnoreFileType) fileType;
            }
        }
        return null;
    }

    /**
     * Checks if file is under given directory.
     *
     * @param file      file
     * @param directory directory
     * @return file is under directory
     */
    public static boolean isUnder(VirtualFile file, VirtualFile directory) {
        return VfsUtilCore.isUnder(file, Sets.newHashSet(directory));
    }

    /**
     * Converts {@link Color} to the hex string.
     *
     * @param color current color
     * @return hex string
     */
    public static String colorToHexString(Color color) {
        return String.format("#%06X", (0xFFFFFF & color.getRGB()));
    }
}
