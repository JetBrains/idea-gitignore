/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import git4idea.config.GitVcsApplicationSettings;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds util methods for calling external executables (i.e. git/hg)
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.4
 */
public class ExternalExec {
    /** Private constructor to prevent creating {@link Icons} instance. */
    private ExternalExec() {
    }

    /** Checks if Git plugin is enabled. */
    private static final boolean GIT_ENABLED = Utils.isGitPluginEnabled();

    /** Git command to get user's excludesfile path. */
    private static final String GIT_CONFIG_EXCLUDES_FILE = "config --global core.excludesfile";

    /** Git command to list unversioned files. */
    private static final String GIT_UNIGNORED_FILES = "clean -dn";

    /** Prefix to remove from the {@link #GIT_UNIGNORED_FILES} command's result. */
    private static final String GIT_UNIGNORED_FILES_PREFIX = "Would remove";

    /**
     * Returns {@link VirtualFile} instance of the Git excludes file if available.
     *
     * @return Git excludes file
     */
    @Nullable
    public static VirtualFile getGitExcludesFile() {
        return run(GitLanguage.INSTANCE, GIT_CONFIG_EXCLUDES_FILE, null, new Reader<VirtualFile>() {
            @Override
            @Nullable
            VirtualFile read(@NotNull BufferedReader reader) throws IOException {
                String path = Utils.resolveUserDir(reader.readLine());
                return StringUtil.isNotEmpty(path) ? VfsUtil.findFileByIoFile(new File(path), true) : null;
            }
        });
    }

    /**
     * Returns list of unignored files for the given directory.
     *
     * @param language to check
     * @param directory current directory
     * @return unignored files list
     */
    @NotNull
    public static List<String> getUnignoredFiles(@NotNull IgnoreLanguage language, @NotNull VirtualFile directory) {
        return ContainerUtil.notNullize(run(language, GIT_UNIGNORED_FILES, directory, new Reader<List<String>>() {
            @Override
            @NotNull
            List<String> read(@NotNull BufferedReader reader) throws IOException {
                final ArrayList<String> result = ContainerUtil.newArrayList();
                String line;

                while ((line = reader.readLine()) != null) {
                    String entry = StringUtil.trim(StringUtil.trimStart(line, GIT_UNIGNORED_FILES_PREFIX));
                    ContainerUtil.addIfNotNull(entry, result);
                }

                return result;
            }
        }));
    }

    /**
     * Returns path to the {@link IgnoreLanguage} binary or null if not available.
     * Currently only  {@link GitLanguage} is supported.
     *
     * @param language current language
     * @return path to binary
     */
    @Nullable
    private static String bin(@NotNull IgnoreLanguage language) {
        if (GitLanguage.INSTANCE.equals(language) && GIT_ENABLED) {
            final String bin = GitVcsApplicationSettings.getInstance().getPathToGit();
            return StringUtil.nullize(bin);
        }
        return null;
    }

    /**
     * Runs {@link IgnoreLanguage} executable with the given command and current working directory.
     *
     * @param language current language
     * @param command to call
     * @param directory current working directory
     * @param reader {@link BufferedReader} wrapper
     * @param <T> return type
     * @return result of the call
     */
    @Nullable
    private static <T> T run(@NotNull IgnoreLanguage language, @NotNull String command, @Nullable VirtualFile directory, @NotNull Reader<T> reader) {
        final String bin = bin(language);
        if (bin == null) {
            return null;
        }

        try {
            File workingDirectory = directory != null ? new File(directory.getPath()) : null;
            Process pr = Runtime.getRuntime().exec(bin + " " + command, null, workingDirectory);
            pr.waitFor();

            ProcessWithTimeout processWithTimeout = new ProcessWithTimeout(pr);
            int exitCode = processWithTimeout.waitForProcess(3000);
            if (exitCode == Integer.MIN_VALUE) {
                pr.destroy();
            }

            return reader.read(new BufferedReader(new InputStreamReader(pr.getInputStream())));
        } catch (IOException ignored) {
        } catch (InterruptedException ignored) {
        }

        return null;
    }

    /**
     * {@link BufferedReader} wrapper class
     *
     * @param <T> type
     */
    private abstract static class Reader<T> {
        @Nullable
        abstract T read(@NotNull BufferedReader reader) throws IOException;
    }
}
