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

package mobi.hsz.idea.gitignore.lang.kind;

import com.intellij.lang.Language;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.config.GitVcsApplicationSettings;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Gitignore {@link Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class GitLanguage extends IgnoreLanguage {
    /** The {@link GitLanguage} instance. */
    public static final GitLanguage INSTANCE = new GitLanguage();

    /** The outer file. */
    public static VirtualFile OUTER_FILE;

    /** Flag to mark that outer file was fetched. */
    private static boolean OUTER_FILE_FETCHED = false;

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private GitLanguage() {
        super("Git", "gitignore", Icons.GIT);
    }

    /** Language file type. */
    @Override
    public IgnoreFileType getFileType() {
        return GitFileType.INSTANCE;
    }

    /**
     * Returns path to the global excludes file.
     *
     * @return excludes file path
     */
    @Nullable
    @Override
    public VirtualFile getOuterFile() {
        if (OUTER_FILE_FETCHED) {
            return OUTER_FILE;
        }
        if (Utils.isGitPluginEnabled()) {
            final String bin = GitVcsApplicationSettings.getInstance().getPathToGit();
            if (StringUtil.isNotEmpty(bin)) {
                try {
                    Process pr = Runtime.getRuntime().exec(bin + " config --global core.excludesfile");
                    pr.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String path = Utils.resolveUserDir(reader.readLine());
                    if (StringUtil.isNotEmpty(path)) {
                        OUTER_FILE = VfsUtil.findFileByIoFile(new File(path), true);
                    }
                }
                catch (IOException ignored) {}
                catch (InterruptedException ignored) {}
            }
        }
        OUTER_FILE_FETCHED = true;
        return OUTER_FILE;
    }

    /** The Git specific directory. */
    public String getGitDirectory() {
        return ".git";
    }
}
