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

package mobi.hsz.idea.gitignore.file.type.kind;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.kind.GitExcludeLanguage;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Describes Git exclude file type.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.4
 */
public class GitExcludeFileType extends IgnoreFileType {
    /** Contains {@link GitExcludeFileType} singleton. */
    public static final GitExcludeFileType INSTANCE = new GitExcludeFileType();

    /** Private constructor to prevent direct object creation. */
    private GitExcludeFileType() {
        super(GitExcludeLanguage.INSTANCE);
    }

    @Nullable
    public static VirtualFile getWorkingDirectory(@NotNull Project project, @NotNull VirtualFile outerFile) {
        final VirtualFile baseDir = Utils.guessProjectDir(project);
        if (baseDir == null) {
            return null;
        }

        final VirtualFile infoDir = baseDir.findFileByRelativePath(".git/info");
        if (infoDir != null && Utils.isUnder(outerFile, infoDir)) {
            return baseDir;
        }

        final VirtualFile gitModules = baseDir.findFileByRelativePath(".git/modules");
        if (gitModules != null && Utils.isUnder(outerFile, gitModules)) {
            String path = Utils.getRelativePath(gitModules, outerFile.getParent().getParent());
            if (path != null) {
                return baseDir.findFileByRelativePath(path);
            }
        }

        return null;
    }
}
