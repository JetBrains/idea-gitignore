/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.lang.bzrignore;

import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.bzrignore.BzrignoreFile;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Bzrignore {@link com.intellij.lang.Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.9
 */
public class BzrignoreLanguage extends IgnoreLanguage {
    /** The {@link BzrignoreLanguage} instance. */
    public static final BzrignoreLanguage INSTANCE = new BzrignoreLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private BzrignoreLanguage() {
        super("Bzrignore");
    }

    /** The {@link BzrignoreLanguage} instance. */
    @Override
    public BzrignoreLanguage getInstance() {
        return INSTANCE;
    }

    /** The Bzrignore language name. */
    @Override
    public String getName() {
        return "Bzrignore";
    }

    /** The Bzrignore file extension suffix. */
    @Override
    public String getExtension() {
        return "bzrignore";
    }

    /** The Bzrignore file extension. */
    @Override
    public String getFilename() {
        return DOT + getExtension();
    }

    /** The Bzrignore icon. */
    @Override
    public Icon getIcon() {
        return Icons.BZRIGNORE;
    }

    /** Creates {@link BzrignoreFile} instance. */
    @Override
    public IgnoreFile createFile(@NotNull FileViewProvider viewProvider) {
        return new BzrignoreFile(viewProvider);
    }
}
