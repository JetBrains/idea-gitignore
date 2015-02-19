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

package mobi.hsz.idea.gitignore.lang;

import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Gitignore {@link Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class IgnoreLanguage extends Language {
    /** The {@link IgnoreLanguage} instance. */
    public static final IgnoreLanguage INSTANCE = new IgnoreLanguage();

    /** The dot. */
    public static final String DOT = ".";

    /** Ignore languages group name. */
    @NonNls
    public static final String GROUP = "IGNORE_GROUP";

    /** The {@link IgnoreLanguage} instance. */
    public IgnoreLanguage getInstance() {
        return INSTANCE;
    }

    /** The Gitignore language name. */
    public String getName() {
        return "Ignore";
    }

    /** The Gitignore file extension suffix. */
    public String getExtension() {
        return "ignore";
    }

    /** The Gitignore file extension. */
    public String getFilename() {
        return DOT + getExtension();
    }

    /** The GitignoreLanguage icon. */
    public Icon getIcon() {
        return null;
    }

    /** Creates {@link IgnoreFile} instance. */
    public IgnoreFile createFile(@NotNull FileViewProvider viewProvider) {
        return new IgnoreFile(viewProvider, IgnoreFileType.INSTANCE);
    }

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    protected IgnoreLanguage() {
        super("Ignore");
    }

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    protected IgnoreLanguage(String name) {
        super(name);
    }
}
