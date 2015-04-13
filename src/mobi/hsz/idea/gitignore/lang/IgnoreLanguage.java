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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;

/**
 * Gitignore {@link Language} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class IgnoreLanguage extends Language {
    /**
     * The {@link IgnoreLanguage} instance.
     */
    public static final IgnoreLanguage INSTANCE = new IgnoreLanguage();

    /**
     * The dot.
     */
    @NonNls
    public static final String DOT = ".";

    /**
     * Ignore languages group name.
     */
    @NonNls
    public static final String GROUP = "IGNORE_GROUP";

    /**
     * The Ignore file extension suffix.
     */
    private final String extension;

    /**
     * The GitignoreLanguage icon.
     */
    private final Icon icon;

    /**
     * {@link IgnoreLanguage} is a non-instantiable static class.
     */
    protected IgnoreLanguage() {
        this("Ignore", "ignore", null);
    }

    /**
     * {@link IgnoreLanguage} is a non-instantiable static class.
     */
    protected IgnoreLanguage(@NotNull String name, @NotNull String extension, @Nullable Icon icon) {
        super(name);
        this.extension = extension;
        this.icon = icon;
    }

    /**
     * Returns Ignore file extension suffix.
     *
     * @return extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * The Gitignore file extension.
     */
    public String getFilename() {
        return DOT + getExtension();
    }

    /**
     * Returns Ignore file icon.
     *
     * @return icon
     */
    @Nullable
    public Icon getIcon() {
        return icon;
    }

    /**
     * Language file type.
     */
    public IgnoreFileType getFileType() {
        return IgnoreFileType.INSTANCE;
    }

    /**
     * Creates {@link IgnoreFile} instance.
     */
    public IgnoreFile createFile(@NotNull final FileViewProvider viewProvider) {
        return new IgnoreFile(viewProvider, getFileType());
    }

    /**
     * Returns <code>true</code> if `syntax: value` entry is supported by the language (i.e. Mercurial).
     *
     * @return <code>true</code> if `syntax: value` entry is supported
     */
    public boolean isSyntaxSupported() {
        return false;
    }

    /**
     * Returns default language syntax.
     *
     * @return default syntax
     */
    @NotNull
    public IgnoreBundle.Syntax getDefaultSyntax() {
        return IgnoreBundle.Syntax.GLOB;
    }

    /**
     * Returns path to the global excludes file.
     *
     * @param project current project
     * @return excludes file path
     */
    @Nullable
    public VirtualFile getOuterFile(@NotNull final Project project) {
        return null;
    }

    /**
     * Checks is language is enabled or with {@link IgnoreSettings}.
     *
     * @return language is enabled
     */
    public boolean isEnabled() {
        HashMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = IgnoreSettings.getInstance().getLanguagesSettings().get(this);
        boolean value = false;
        if (data != null) {
            value = Boolean.valueOf(data.get(IgnoreSettings.IgnoreLanguagesSettings.KEY.ENABLE).toString());
        }
        return value;
    }

    /**
     * Checks if creating new file for given language is allowed with the settings.
     *
     * @return new file action is allowed
     */
    public boolean isNewAllowed() {
        HashMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = IgnoreSettings.getInstance().getLanguagesSettings().get(this);
        boolean value = false;
        if (data != null) {
            value = Boolean.valueOf(data.get(IgnoreSettings.IgnoreLanguagesSettings.KEY.NEW_FILE).toString());
        }
        return value;
    }

    /**
     * Language is related to the VCS.
     *
     * @return is VCS
     */
    public boolean isVCS() {
        return true;
    }
}
