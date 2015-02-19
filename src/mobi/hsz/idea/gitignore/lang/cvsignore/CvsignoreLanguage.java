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

package mobi.hsz.idea.gitignore.lang.cvsignore;

import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.cvsignore.CvsignoreFile;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Cvsignore {@link com.intellij.lang.Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.9
 */
public class CvsignoreLanguage extends IgnoreLanguage {
    /** The {@link CvsignoreLanguage} instance. */
    public static final CvsignoreLanguage INSTANCE = new CvsignoreLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private CvsignoreLanguage() {
        super("Cvsignore");
    }

    /** The {@link CvsignoreLanguage} instance. */
    @Override
    public CvsignoreLanguage getInstance() {
        return INSTANCE;
    }

    /** The Cvsignore language name. */
    @Override
    public String getName() {
        return "Cvsignore";
    }

    /** The Cvsignore file extension suffix. */
    @Override
    public String getExtension() {
        return "cvsignore";
    }

    /** The Cvsignore file extension. */
    @Override
    public String getFilename() {
        return DOT + getExtension();
    }

    /** The Cvsignore icon. */
    @Override
    public Icon getIcon() {
        return Icons.CVSIGNORE;
    }

    /** Creates {@link CvsignoreFile} instance. */
    @Override
    public IgnoreFile createFile(@NotNull FileViewProvider viewProvider) {
        return new CvsignoreFile(viewProvider);
    }
}
