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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.indexing.IgnoreFilesIndex;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.outer.OuterIgnoreLoaderComponent.OuterFileFetcher;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import mobi.hsz.idea.gitignore.util.exec.ExternalExec;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

/**
 * Gitignore {@link IgnoreLanguage} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class GitLanguage extends IgnoreLanguage {
    private boolean fetched = false;

    /** The {@link GitLanguage} instance. */
    public static final GitLanguage INSTANCE = new GitLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private GitLanguage() {
        super("Git", "gitignore", ".git", Icons.GIT, new OuterFileFetcher[]{

                // Outer file fetched from the `git config core.excludesfile`.
                project -> ContainerUtil.newArrayList(ExternalExec.getGitExcludesFile())

        });
    }

    /**
     * Language file type.
     *
     * @return {@link GitFileType} instance
     */
    @NotNull
    @Override
    public IgnoreFileType getFileType() {
        return GitFileType.INSTANCE;
    }

    /**
     * Defines if {@link GitLanguage} supports outer ignore files.
     *
     * @return supports outer ignore files
     */
    @Override
    public boolean isOuterFileSupported() {
        return true;
    }

    /**
     * Returns outer files for the current language.
     *
     * @param project current project
     * @return outer files
     */
    @NotNull
    @Override
    public Set<VirtualFile> getOuterFiles(@NotNull final Project project, boolean dumb) {
        final int key = new HashCodeBuilder().append(project).append(getFileType()).toHashCode();

        if (dumb || (fetched && outerFiles.get(key) != null)) {
            return super.getOuterFiles(project, true);
        }

        fetched = true;
        final Set<VirtualFile> parentFiles = super.getOuterFiles(project, false);
        final ArrayList<VirtualFile> files = ContainerUtil.newArrayList(ContainerUtil.filter(
                IgnoreFilesIndex.getFiles(project, GitExcludeFileType.INSTANCE),
                virtualFile -> Utils.isInProject(virtualFile, project)
        ));

        ContainerUtil.addAllNotNull(parentFiles, files);
        return outerFiles.getOrElse(key, ContainerUtil.newHashSet());
    }
}
