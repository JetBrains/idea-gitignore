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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    private String donationShown = "";

    /** Lists all user defined templates. */
    private List<UserTemplate> userTemplates = new ArrayList<UserTemplate>();


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

        Element templates = new Element("userTemplates");
        for (UserTemplate userTemplate : userTemplates) {
            Element templateElement = new Element("template");
            templateElement.setAttribute("name", userTemplate.getName());
            templateElement.addContent(userTemplate.getContent());
            templates.addContent(templateElement);
        }
        element.addContent(templates);

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

        userTemplates.clear();
        Element templates = element.getChild("userTemplates");
        if (templates != null) {
            for (Element template : templates.getChildren()) {
                userTemplates.add(new UserTemplate(template.getAttributeValue("name"), template.getText()));
            }
        }
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

    /**
     * Gets the list of user defined templates.
     *
     * @return user templates
     */
    public List<UserTemplate> getUserTemplates() {
        return userTemplates;
    }

    /**
     * Sets the list of user defined templates.
     *
     * @param userTemplates user templates
     */
    public void setUserTemplates(@NotNull List<UserTemplate> userTemplates) {
        this.userTemplates.clear();
        this.userTemplates.addAll(userTemplates);
    }

    /**
     * User defined template model.
     */
    public static class UserTemplate {
        /** Template name. */
        private String name = "";

        /** Template content. */
        private String content = "";

        public UserTemplate() {
        }

        public UserTemplate(@NotNull String name, @NotNull String content) {
            this.name = name;
            this.content = content;
        }

        /**
         * Sets template name.
         *
         * @param name template name
         */
        public void setName(@NotNull String name) {
            this.name = name;
        }

        /**
         * Gets template name.
         *
         * @return template name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets template content.
         *
         * @param content template content
         */
        public void setContent(@NotNull String content) {
            this.content = content;
        }

        /**
         * Gets template content.
         *
         * @return template content
         */
        public String getContent() {
            return content;
        }

        /**
         * Returns a string representation of the object.
         *
         * @return string representation
         */
        @Override
        public String toString() {
            return this.name;
        }

        /**
         * Checks if template has set name or content.
         *
         * @return true if name or content is filled
         */
        public boolean isEmpty() {
            return this.name.isEmpty() && this.content.isEmpty();
        }

        /**
         * Checks if objects are equal.
         *
         * @param obj another template
         * @return templates are equal
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UserTemplate)) {
                return false;
            }
            if (obj == this) {
                return true;
            }

            UserTemplate t = (UserTemplate) obj;
            return (getName() != null && getName().equals(t.getName()) || (getName() == null && t.getName() == null))
                    && (getContent() != null && getContent().equals(t.getContent()) || (getContent() == null && t.getContent() == null));
        }
    }
}
