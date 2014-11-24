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

package mobi.hsz.idea.gitignore.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Describes Gitignore file type.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class GitignoreFileType extends LanguageFileType {
    /** Contains {@link GitignoreFileType} singleton. */
    public static final GitignoreFileType INSTANCE = new GitignoreFileType();

    /** Private constructor to prevent direct object creation. */
    private GitignoreFileType() {
        super(GitignoreLanguage.INSTANCE);
    }

    /**
     * Returns the name of the file type. The name must be unique among all file types registered in the system.
     * @return The file type name.
     */
    @NotNull
    @Override
    public String getName() {
        return GitignoreLanguage.NAME + " file";
    }

    /**
     * Returns the user-readable description of the file type.
     * @return The file type description.
     */
    @NotNull
    @Override
    public String getDescription() {
        return GitignoreLanguage.NAME + " file";
    }

    /**
     * Returns the default extension for files of the type.
     * @return The extension, not including the leading '.'.
     */
    @NotNull
    @Override
    public String getDefaultExtension() {
        return GitignoreLanguage.EXTENSION;
    }

    /**
     * Returns the icon used for showing files of the type.
     * @return The icon instance, or null if no icon should be shown.
     */
    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
