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

package mobi.hsz.idea.gitignore;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NonNls;

/**
 * Gitignore {@link Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class GitignoreLanguage extends Language {

    /**
     * The {@link GitignoreLanguage} instance.
     */
    public static final GitignoreLanguage INSTANCE = new GitignoreLanguage();

    /**
     * The Gitignore language name.
     */
    @NonNls
    public static final String NAME = "Gitignore";

    /**
     * The Gitignore file extension suffix.
     */
    @NonNls
    public static final String EXTENSION = "gitignore";

    /**
     * The Gitignore file extension.
     */
    @NonNls
    public static final String FILENAME = "." + EXTENSION;

    /**
     * The Gitignore directory name.
     */
    @NonNls
    public static final String GIT_DIRECTORY = ".git";

    /**
     * {@link GitignoreLanguage} is a non-instantiable static class.
     */
    private GitignoreLanguage() {
        super(NAME);
    }
}
