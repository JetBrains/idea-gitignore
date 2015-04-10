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
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.Listenable;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent global settings object for the Ignore plugin.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
@State(
        name = "IgnoreSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/ignore.xml")
)
public class IgnoreSettings implements PersistentStateComponent<Element>, Listenable<IgnoreSettings.Listener> {

    /**
     * Current plugin version.
     */
    private static final String PLUGIN_VERSION = Utils.getMinorVersion();

    private static final UserTemplate DEFAULT_TEMPLATE = new UserTemplate(
            IgnoreBundle.message("settings.userTemplates.default.name"),
            IgnoreBundle.message("settings.userTemplates.default.content")
    );

    /**
     * Notify about missing Gitignore file in the project.
     */
    private boolean missingGitignore = true;

    /**
     * Enable ignored file status coloring.
     */
    private boolean ignoredFileStatus = true;

    /**
     * Enable outer ignore rules.
     */
    private boolean outerIgnoreRules = true;

    /**
     * Settings related to the {@link IgnoreLanguage}.
     */
    private IgnoreLanguagesSettings languagesSettings = new IgnoreLanguagesSettings() {{
        for (IgnoreLanguage fileType : IgnoreBundle.LANGUAGES) {
            put(fileType, new HashMap<KEY, Object>() {{
                put(KEY.NEW_FILE, true);
                put(KEY.ENABLE, true);
            }});
        }
    }};

    /**
     * Shows information about donation.
     */
    private String donationShown = "";

    /**
     * Lists all user defined templates.
     */
    private final List<UserTemplate> userTemplates = ContainerUtil.newArrayList(DEFAULT_TEMPLATE);

    /**
     * Listeners list.
     */
    private final List<Listener> listeners = ContainerUtil.newArrayList();

    /**
     * Get the instance of this service.
     *
     * @return the unique {@link IgnoreSettings} instance.
     */
    public static IgnoreSettings getInstance() {
        return ServiceManager.getService(IgnoreSettings.class);
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
        final Element element = new Element("IgnoreSettings");
        element.setAttribute("missingGitignore", Boolean.toString(missingGitignore));
        element.setAttribute("donationShown", donationShown);
        element.setAttribute("ignoredFileStatus", Boolean.toString(ignoredFileStatus));
        element.setAttribute("outerIgnoreRules", Boolean.toString(outerIgnoreRules));

        Element languagesElement = new Element("languages");
        for (Map.Entry<IgnoreLanguage, HashMap<IgnoreLanguagesSettings.KEY, Object>> entry : languagesSettings.entrySet()) {
            Element languageElement = new Element("language");
            languageElement.setAttribute("id", entry.getKey().getID());
            for (Map.Entry<IgnoreLanguagesSettings.KEY, Object> data : entry.getValue().entrySet()) {
                languageElement.setAttribute(data.getKey().name(), data.getValue().toString());
            }
            languagesElement.addContent(languageElement);
        }
        element.addContent(languagesElement);

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

        value = element.getAttributeValue("ignoredFileStatus");
        if (value != null) ignoredFileStatus = Boolean.parseBoolean(value);

        value = element.getAttributeValue("outerIgnoreRules");
        if (value != null) outerIgnoreRules = Boolean.parseBoolean(value);

        Element languagesElement = element.getChild("languages");
        if (languagesElement != null) {
            languagesSettings.clear();
            for (Element languageElement : languagesElement.getChildren()) {
                HashMap<IgnoreLanguagesSettings.KEY, Object> data = ContainerUtil.newHashMap();
                for (IgnoreLanguagesSettings.KEY key : IgnoreLanguagesSettings.KEY.values()) {
                    data.put(key, languageElement.getAttributeValue(key.name()));
                }
                String id = languageElement.getAttributeValue("id");
                IgnoreLanguage language = IgnoreBundle.LANGUAGES.get(id);
                languagesSettings.put(language, data);
            }
        }

        Element templates = element.getChild("userTemplates");
        if (templates != null) {
            userTemplates.clear();
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
        this.notifyOnChange("missingGitignore", this.missingGitignore, missingGitignore);
        this.missingGitignore = missingGitignore;
    }

    /**
     * Check if ignored file status coloring is enabled.
     *
     * @return ignored file status coloring is enabled
     */
    public boolean isIgnoredFileStatus() {
        return ignoredFileStatus;
    }

    /**
     * Sets ignored file status coloring.
     *
     * @param ignoredFileStatus ignored file status coloring
     */
    public void setIgnoredFileStatus(boolean ignoredFileStatus) {
        this.notifyOnChange("ignoredFileStatus", this.ignoredFileStatus, ignoredFileStatus);
        this.ignoredFileStatus = ignoredFileStatus;
    }

    /**
     * Check if outer ignore rules is enabled.
     *
     * @return ignored file status coloring is enabled
     */
    public boolean isOuterIgnoreRules() {
        return outerIgnoreRules;
    }

    /**
     * Sets outer ignore rules.
     *
     * @param outerIgnoreRules ignored file status coloring
     */
    public void setOuterIgnoreRules(boolean outerIgnoreRules) {
        this.notifyOnChange("outerIgnoreRules", this.outerIgnoreRules, outerIgnoreRules);
        this.outerIgnoreRules = outerIgnoreRules;
    }

    /**
     * Gets the {@link IgnoreLanguage} settings.
     *
     * @return fileType settings
     */
    public IgnoreLanguagesSettings getLanguagesSettings() {
        return languagesSettings;
    }

    /**
     * Sets the {@link IgnoreLanguage} settings.
     *
     * @param languagesSettings languagesSettings
     */
    public void setLanguagesSettings(IgnoreLanguagesSettings languagesSettings) {
        this.notifyOnChange("languages", this.languagesSettings, languagesSettings);
        this.languagesSettings = languagesSettings;
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
        this.notifyOnChange("donationShown", this.donationShown, PLUGIN_VERSION);
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
        this.notifyOnChange("userTemplates", this.userTemplates, userTemplates);
        this.userTemplates.clear();
        this.userTemplates.addAll(userTemplates);
    }

    /**
     * Add the given listener. The listener will be executed in the containing instance's thread.
     *
     * @param listener listener to add
     */
    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Remove the given listener.
     *
     * @param listener listener to remove
     */
    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies listeners about the changes.
     *
     * @param key      changed property key
     * @param oldValue new value
     * @param newValue new value
     */
    private void notifyOnChange(String key, Object oldValue, Object newValue) {
        if (!newValue.equals(oldValue)) {
            for (Listener listener : listeners) {
                listener.onChange(key, newValue);
            }
        }
    }

    /**
     * Listener interface for onChange event.
     */
    public static interface Listener {
        public void onChange(@NotNull String key, Object value);
    }

    /**
     * User defined template model.
     */
    public static class UserTemplate {
        /**
         * Template name.
         */
        private String name = "";

        /**
         * Template content.
         */
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

    public static class IgnoreLanguagesSettings extends LinkedHashMap<IgnoreLanguage, HashMap<IgnoreLanguagesSettings.KEY, Object>> {
        public enum KEY {
            NEW_FILE, ENABLE
        }

        /**
         * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
         * values themselves are not cloned.
         *
         * @return a shallow copy of this map
         */
        @Override
        public IgnoreLanguagesSettings clone() {
            IgnoreLanguagesSettings copy = (IgnoreLanguagesSettings) super.clone();
            for (HashMap.Entry<IgnoreLanguage, HashMap<IgnoreLanguagesSettings.KEY, Object>> entry : copy.entrySet()) {
                @SuppressWarnings("unchecked")
                HashMap<IgnoreLanguagesSettings.KEY, Object> data = (HashMap<KEY, Object>) entry.getValue().clone();

                copy.put(entry.getKey(), data);
            }
            return copy;
        }
    }
}
