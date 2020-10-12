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

package mobi.hsz.idea.gitignore.util.exec.parser;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Parser for the {@link mobi.hsz.idea.gitignore.util.exec.ExternalExec}#GIT_CONFIG_EXCLUDES_FILE command that
 * returns excludes Git file instance.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public class GitExcludesOutputParser extends ExecutionOutputParser<VirtualFile> {
    /**
     * Parses output and returns {@link VirtualFile} instance of the GitFileType.
     *
     * @param text input data
     * @return excludes ignore file instance
     */
    @Nullable
    @Override
    protected VirtualFile parseOutput(@NotNull final String text) {
        final String path = Utils.resolveUserDir(text);
        return StringUtil.isNotEmpty(path) ? VfsUtil.findFileByIoFile(new File(path), true) : null;
    }
}
