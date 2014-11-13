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

package mobi.hsz.idea.gitignore.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

/**
 * Persistent global settings object for the Gitignore plugin.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
@State(
        name = "GitignoreSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/gitignore.xml")
)
public class GitignoreSettings {

    /** Notify about missing Gitignore file in the project. */
    private boolean missingGitignore = true;


    /**
     * Get the instance of this service.
     *
     * @return the unique {@link GitignoreSettings} instance.
     */
    public static GitignoreSettings getInstance() {
        return ServiceManager.getService(GitignoreSettings.class);
    }


    /**
     * Notify about missing Gitignore file in the project.
     *
     * @return {@link #missingGitignore}
     */
    public boolean isMissingGitignore() {
        return missingGitignore;
    }

    /**
     * Notify about missing Gitignore file in the project.
     *
     * @param missingGitignore notify about missing Gitignore file in the project

     */
    public void setMissingGitignore(boolean missingGitignore) {
        this.missingGitignore = missingGitignore;
    }
}
