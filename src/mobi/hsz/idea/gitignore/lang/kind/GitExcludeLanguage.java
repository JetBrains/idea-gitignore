/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.lang.kind;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.outer.OuterIgnoreLoaderComponent.OuterFileFetcher;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Gitignore Exclude {@link IgnoreLanguage} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.4
 */
public class GitExcludeLanguage extends IgnoreLanguage {
    /** The {@link GitExcludeLanguage} instance. */
    public static final GitExcludeLanguage INSTANCE = new GitExcludeLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private GitExcludeLanguage() {
        super("Git exclude", "exclude", ".git", Icons.GIT, new OuterFileFetcher[]{

                // `exclude` files located in .git directory
                new OuterFileFetcher() {
                    /** Relative path to the exclude file. */
                    @NonNls
                    private static final String EXCLUDE = "info/exclude";

                    @NotNull
                    @Override
                    public Collection<VirtualFile> fetch(@NotNull Project project) {
                        final Collection<VirtualFile> files = ContainerUtil.newArrayList();
                        final VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
                        if (baseDir == null) {
                            return files;
                        }

                        final VirtualFile root = baseDir.findChild(".git");
                        return processExcludes(root, files);
                    }

                    /**
                     * Recursively finds exclude files in given root directory.
                     *
                     * @param root  current root
                     * @param files collection of {@link VirtualFile}
                     * @return exclude files collection
                     */
                    @NotNull
                    private Collection<VirtualFile> processExcludes(@Nullable final VirtualFile root,
                                                                    @NotNull final Collection<VirtualFile> files) {
                        if (root != null) {
                            ContainerUtil.addIfNotNull(files, root.findFileByRelativePath(EXCLUDE));

                            final VirtualFile modules = root.findChild("modules");
                            if (modules != null) {
                                VfsUtil.visitChildrenRecursively(modules, new VirtualFileVisitor() {
                                    @Override
                                    public boolean visitFile(@NotNull VirtualFile dir) {
                                        if (dir.findChild("index") != null) {
                                            processExcludes(dir, files);
                                            return false;
                                        }
                                        return dir.isDirectory();
                                    }
                                });
                            }
                        }

                        return files;
                    }
                }

        });
    }

    /**
     * Language file type.
     *
     * @return {@link GitExcludeFileType} instance
     */
    @NotNull
    @Override
    public IgnoreFileType getFileType() {
        return GitExcludeFileType.INSTANCE;
    }

    /**
     * The Gitignore exclude filename.
     *
     * @return filename
     */
    @NotNull
    @Override
    public String getFilename() {
        return super.getExtension();
    }

    /**
     * Defines if {@link GitExcludeLanguage} supports outer ignore files.
     *
     * @return supports outer ignore files
     */
    @Override
    public boolean isOuterFileSupported() {
        return true;
    }

    /**
     * Returns fixed directory for the given {@link IgnoreLanguage}.
     *
     * @param project current project
     * @return fixed directory
     */
    @Nullable
    @Override
    public VirtualFile getFixedDirectory(@NotNull Project project) {
        final VirtualFile projectDir = ProjectUtil.guessProjectDir(project);
        if (projectDir == null) {
            return null;
        }
        return projectDir.findFileByRelativePath(getVcsDirectory() + "/info");
    }
}
