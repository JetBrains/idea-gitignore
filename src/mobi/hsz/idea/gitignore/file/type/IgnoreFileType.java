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

package mobi.hsz.idea.gitignore.file.type;

import com.intellij.openapi.fileTypes.LanguageFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Describes Ignore file type.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class IgnoreFileType extends LanguageFileType {
    /** Contains {@link IgnoreFileType} singleton. */
    @NotNull
    public static final IgnoreFileType INSTANCE = new IgnoreFileType();

    /** Current file type language. */
    @NotNull
    private final IgnoreLanguage language;

    /** Protected constructor to prevent direct object creation. */
    protected IgnoreFileType() {
        this(IgnoreLanguage.INSTANCE);
    }

    /** Protected constructor to prevent direct object creation. */
    protected IgnoreFileType(@NotNull IgnoreLanguage language) {
        super(language);
        this.language = language;
    }

    /**
     * Returns the name of the file type. The name must be unique among all file types registered in the system.
     *
     * @return The file type name.
     */
    @NotNull
    @Override
    public String getName() {
        return language.getDisplayName() + " file";
    }

    /**
     * Returns the name of the language.
     *
     * @return The language name.
     */
    @NotNull
    public String getLanguageName() {
        return language.getDisplayName();
    }

    /**
     * Returns the user-readable description of the file type.
     *
     * @return The file type description.
     */
    @NotNull
    @Override
    public String getDescription() {
        return language.getDisplayName() + " file";
    }

    /**
     * Returns the default extension for files of the type.
     *
     * @return The extension, not including the leading '.'.
     */
    @NotNull
    @Override
    public String getDefaultExtension() {
        return language.getExtension();
    }

    /**
     * Returns the icon used for showing files of the type.
     *
     * @return The icon instance, or null if no icon should be shown.
     */
    @Nullable
    @Override
    public Icon getIcon() {
        return language.getIcon();
    }

    /**
     * Returns {@see IgnoreLanguage} instance.
     *
     * @return associated language.
     */
    @NotNull
    public IgnoreLanguage getIgnoreLanguage() {
        return language;
    }

    /**
     * Returns hashCode of the current {@link IgnoreLanguage}.
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return language.getDisplayName().hashCode();
    }
}
