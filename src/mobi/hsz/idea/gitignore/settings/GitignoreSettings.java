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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

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
public class GitignoreSettings implements PersistentStateComponent<Element> {

    /** Current plugin version. */
    private static final String PLUGIN_VERSION = Utils.getPlugin().getVersion();

    /** Notify about missing Gitignore file in the project. */
    private boolean missingGitignore = true;

    /** Shows information about donation. */
    private String donationShown;


    /**
     * Get the instance of this service.
     *
     * @return the unique {@link GitignoreSettings} instance.
     */
    public static GitignoreSettings getInstance() {
        return ServiceManager.getService(GitignoreSettings.class);
    }

    /**
     * Get the settings state as a DOM element.
     *
     * @return an ready to serialize DOM {@link Element}.
     * @see {@link #loadState(Element)}
     */
    @Nullable
    @Override
    public Element getState() {
        final Element element = new Element("MarkdownSettings");
        element.setAttribute("missingGitignore", Boolean.toString(missingGitignore));
        element.setAttribute("donationShown", donationShown);
        return element;
    }

    /**
     * Load the settings state from the DOM {@link Element}.
     *
     * @param element the {@link Element} to load values from.
     * @see {@link #getState()}
     */
    @Override
    public void loadState(Element element) {
        String value = element.getAttributeValue("missingGitignore");
        if (value != null) missingGitignore = Boolean.parseBoolean(value);
        value = element.getAttributeValue("donationShown");
        if (value != null) donationShown = value;
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

    /**
     * Shows information about donation.
     *
     * @return {@link #donationShown} equals to the {@link #PLUGIN_VERSION}
     */
    public boolean isDonationShown() {
        return donationShown != null && donationShown.equals(PLUGIN_VERSION);
    }

    /**
     * Sets {@link #donationShown} to the {@link #PLUGIN_VERSION} value.
     */
    public void setDonationShown() {
        this.donationShown = PLUGIN_VERSION;
    }
}
