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

package mobi.hsz.idea.gitignore.lang.dockerignore;

import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.dockerignore.DockerignoreFile;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Dockerignore {@link IgnoreLanguage} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class DockerignoreLanguage extends IgnoreLanguage {
    /** The {@link DockerignoreLanguage} instance. */
    public static final DockerignoreLanguage INSTANCE = new DockerignoreLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    protected DockerignoreLanguage() {
        super("Dockerignore");
    }

    /** The {@link DockerignoreLanguage} instance. */
    @Override
    public DockerignoreLanguage getInstance() {
        return INSTANCE;
    }

    /** The Dockerignore language name. */
    @Override
    public String getName() {
        return "Dockerignore";
    }

    /** The Dockerignore file extension suffix. */
    @Override
    public String getExtension() {
        return "dockerignore";
    }

    /** The Dockerignore file extension. */
    @Override
    public String getFilename() {
        return DOT + getExtension();
    }

    /** The DockerignoreLanguage icon. */
    @Override
    public Icon getIcon() {
        return Icons.DOCKERIGNORE;
    }

    /** Creates {@link DockerignoreFile} instance. */
    @Override
    public IgnoreFile createFile(@NotNull FileViewProvider viewProvider) {
        return new DockerignoreFile(viewProvider);
    }
}
