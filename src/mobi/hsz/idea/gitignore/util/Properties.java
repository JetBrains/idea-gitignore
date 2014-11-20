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

package mobi.hsz.idea.gitignore.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

/**
 * {@link Properties} util class that holds project specified settings using {@link PropertiesComponent}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3.3
 */
public class Properties {
    /** Ignore missing gitignore property key. */
    private static final String PROP_IGNORE_MISSING_GITIGNORE = "ignore_missing_gitignore";

    /** Private constructor to prevent creating {@link Properties} instance. */
    private Properties() {
    }

    /**
     * Checks value of {@link #PROP_IGNORE_MISSING_GITIGNORE} key in {@link PropertiesComponent}.
     *
     * @param project current project
     * @return {@link #PROP_IGNORE_MISSING_GITIGNORE} value
     */
    public static boolean isIgnoreMissingGitignore(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(PROP_IGNORE_MISSING_GITIGNORE, false);
    }

    /**
     * Sets value of {@link #PROP_IGNORE_MISSING_GITIGNORE} key in {@link PropertiesComponent} to <code>true</code>.
     *
     * @param project current project
     */
    public static void setIgnoreMissingGitignore(Project project) {
        PropertiesComponent.getInstance(project).setValue(PROP_IGNORE_MISSING_GITIGNORE, Boolean.TRUE.toString());
    }
}
