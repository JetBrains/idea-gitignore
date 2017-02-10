/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.MercurialFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Mercurial {@link IgnoreLanguage} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class MercurialLanguage extends IgnoreLanguage {
    /** The {@link MercurialLanguage} instance. */
    public static final MercurialLanguage INSTANCE = new MercurialLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private MercurialLanguage() {
        super("Mercurial", "hgignore", ".hg", Icons.MERCURIAL);
    }

    /**
     * Language file type.
     *
     * @return {@link MercurialFileType} instance
     */
    @NotNull
    @Override
    public IgnoreFileType getFileType() {
        return MercurialFileType.INSTANCE;
    }

    /**
     * Returns <code>true</code> if `syntax: value` entry is supported by the language (i.e. Mercurial).
     *
     * @return <code>true</code> if `syntax: value` entry is supported
     */
    @Override
    public boolean isSyntaxSupported() {
        return true;
    }

    /**
     * Returns default language syntax.
     *
     * @return default syntax
     */
    @Override
    @NotNull
    public IgnoreBundle.Syntax getDefaultSyntax() {
        return IgnoreBundle.Syntax.REGEXP;
    }
}
