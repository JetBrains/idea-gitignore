/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
     * Settings keys.
     */
    public enum KEY {
        ROOT("IgnoreSettings"), MISSING_GITIGNORE("missingGitignore"), USER_TEMPLATES("userTemplates"),
        USER_TEMPLATES_TEMPLATE("template"), USER_TEMPLATES_NAME("name"), LANGUAGES("languages"),
        LANGUAGES_LANGUAGE("language"), LANGUAGES_ID("id"), IGNORED_FILE_STATUS("ignoredFileStatus"),
        OUTER_IGNORE_RULES("outerIgnoreRules"), OUTER_IGNORE_WRAPPER_HEIGHT("outerIgnoreWrapperHeight"),
        INSERT_AT_CURSOR("insertAtCursor"), VERSION("version");

        private final String key;

        KEY(@NotNull String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return this.key;
        }
    }

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
     * Height of the outer ignore file wrapper panel.
     */
    private int outerIgnoreWrapperHeight = 100;

    /**
     * Enable outer ignore rules.
     */
    private boolean outerIgnoreRules = true;

    /**
     * Insert new entries at the cursor's position or at the document end.
     */
    private boolean insertAtCursor = false;

    /**
     * Plugin version.
     */
    private String version;

    /**
     * Settings related to the {@link IgnoreLanguage}.
     */
    private IgnoreLanguagesSettings languagesSettings = new IgnoreLanguagesSettings() {{
        for (final IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
            put(language, new TreeMap<KEY, Object>() {{
                put(KEY.NEW_FILE, true);
                put(KEY.ENABLE, language.isVCS());
            }});
        }
    }};

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
        final Element element = new Element(KEY.ROOT.toString());
        element.setAttribute(KEY.MISSING_GITIGNORE.toString(), Boolean.toString(missingGitignore));
        element.setAttribute(KEY.IGNORED_FILE_STATUS.toString(), Boolean.toString(ignoredFileStatus));
        element.setAttribute(KEY.OUTER_IGNORE_RULES.toString(), Boolean.toString(outerIgnoreRules));
        element.setAttribute(KEY.OUTER_IGNORE_WRAPPER_HEIGHT.toString(), Integer.toString(outerIgnoreWrapperHeight));
        element.setAttribute(KEY.VERSION.toString(), version);

        Element languagesElement = new Element(KEY.LANGUAGES.toString());
        for (Map.Entry<IgnoreLanguage, TreeMap<IgnoreLanguagesSettings.KEY, Object>> entry : languagesSettings.entrySet()) {
            Element languageElement = new Element(KEY.LANGUAGES_LANGUAGE.toString());
            languageElement.setAttribute(KEY.LANGUAGES_ID.toString(), entry.getKey().getID());
            for (Map.Entry<IgnoreLanguagesSettings.KEY, Object> data : entry.getValue().entrySet()) {
                languageElement.setAttribute(data.getKey().name(), data.getValue().toString());
            }
            languagesElement.addContent(languageElement);
        }
        element.addContent(languagesElement);
        element.addContent(createTemplatesElement(userTemplates));

        // settings fix for 1.1 - can be removed with further releases
        element.setAttribute("settingsFixed", "true");

        return element;
    }

    /**
     * Creates {@link Element} with a list of the {@link UserTemplate} items.
     *
     * @param userTemplates templates
     * @return {@link Element} instance with user templates
     */
    public static Element createTemplatesElement(@NotNull List<UserTemplate> userTemplates) {
        Element templates = new Element(KEY.USER_TEMPLATES.toString());
        for (UserTemplate userTemplate : userTemplates) {
            Element templateElement = new Element(KEY.USER_TEMPLATES_TEMPLATE.toString());
            templateElement.setAttribute(KEY.USER_TEMPLATES_NAME.toString(), userTemplate.getName());
            templateElement.addContent(userTemplate.getContent());
            templates.addContent(templateElement);
        }
        return templates;
    }

    /**
     * Load the settings state from the DOM {@link Element}.
     *
     * @param element the {@link Element} to load values from.
     * @see {@link #getState()}
     */
    @Override
    public void loadState(Element element) {
        String value = element.getAttributeValue(KEY.MISSING_GITIGNORE.toString());
        if (value != null) missingGitignore = Boolean.parseBoolean(value);

        value = element.getAttributeValue(KEY.IGNORED_FILE_STATUS.toString());
        if (value != null) ignoredFileStatus = Boolean.parseBoolean(value);

        value = element.getAttributeValue(KEY.OUTER_IGNORE_RULES.toString());
        if (value != null) outerIgnoreRules = Boolean.parseBoolean(value);

        value = element.getAttributeValue(KEY.VERSION.toString());
        if (value != null) version = value;

        value = element.getAttributeValue(KEY.OUTER_IGNORE_WRAPPER_HEIGHT.toString());
        if (value != null) outerIgnoreWrapperHeight = Integer.parseInt(value);

        Element languagesElement = element.getChild(KEY.LANGUAGES.toString());
        if (languagesElement != null) {
            for (Element languageElement : languagesElement.getChildren()) {
                TreeMap<IgnoreLanguagesSettings.KEY, Object> data = ContainerUtil.newTreeMap();
                for (IgnoreLanguagesSettings.KEY key : IgnoreLanguagesSettings.KEY.values()) {
                    data.put(key, languageElement.getAttributeValue(key.name()));
                }
                String id = languageElement.getAttributeValue(KEY.LANGUAGES_ID.toString());
                IgnoreLanguage language = IgnoreBundle.LANGUAGES.get(id);
                languagesSettings.put(language, data);
            }
        }

        userTemplates.clear();
        userTemplates.addAll(loadTemplates(element));

        // settings fix for 1.1 - can be removed with further releases
        value = element.getAttributeValue("settingsFixed");
        if (value == null) {
            for (IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
                if (!language.isVCS()) {
                    languagesSettings.get(language).put(IgnoreLanguagesSettings.KEY.ENABLE, false);
                }
            }
        }
    }

    /**
     * Loads {@link UserTemplate} objects from the {@link Element}.
     *
     * @param element source
     * @return {@link UserTemplate} list
     */
    @NotNull
    public static List<UserTemplate> loadTemplates(@NotNull Element element) {
        final String key = KEY.USER_TEMPLATES.toString();
        final List<UserTemplate> list = ContainerUtil.newArrayList();
        if (!key.equals(element.getName())) {
            element = element.getChild(key);
        }
        for (Element template : element.getChildren()) {
            list.add(new UserTemplate(template.getAttributeValue(KEY.USER_TEMPLATES_NAME.toString()), template.getText()));
        }
        return list;
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
        this.notifyOnChange(KEY.MISSING_GITIGNORE, this.missingGitignore, missingGitignore);
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
        this.notifyOnChange(KEY.IGNORED_FILE_STATUS, this.ignoredFileStatus, ignoredFileStatus);
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
        this.notifyOnChange(KEY.OUTER_IGNORE_RULES, this.outerIgnoreRules, outerIgnoreRules);
        this.outerIgnoreRules = outerIgnoreRules;
    }

    /**
     * Check if new entries should be inserted at the cursor's position or at the document end.
     *
     * @return entries should be inserted at the cursor's position
     */
    public boolean isInsertAtCursor() {
        return insertAtCursor;
    }

    /**
     * Defines that new entries should be inserted at the cursor's position or at the document end.
     *
     * @param insertAtCursor position
     */
    public void setInsertAtCursor(boolean insertAtCursor) {
        this.notifyOnChange(KEY.INSERT_AT_CURSOR, this.insertAtCursor, insertAtCursor);
        this.insertAtCursor = insertAtCursor;
    }

    /**
     * Returns the height of the outer ignore file wrapper panel.
     *
     * @return wrapper panel height
     */
    public int getOuterIgnoreWrapperHeight() {
        return outerIgnoreWrapperHeight;
    }

    /**
     * Sets plugin version.
     *
     * @param version of the plugin
     */
    public void setVersion(@NotNull String version) {
        this.notifyOnChange(KEY.VERSION, this.version, version);
        this.version = version;
    }

    /**
     * Returns plugin version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets outer ignore rules.
     *
     * @param outerIgnoreWrapperHeight wrapper panel height
     */
    public void setOuterIgnoreWrapperHeight(int outerIgnoreWrapperHeight) {
        this.notifyOnChange(KEY.OUTER_IGNORE_WRAPPER_HEIGHT, this.outerIgnoreWrapperHeight, outerIgnoreWrapperHeight);
        this.outerIgnoreWrapperHeight = outerIgnoreWrapperHeight;
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
        this.notifyOnChange(KEY.LANGUAGES, this.languagesSettings, languagesSettings);
        this.languagesSettings = languagesSettings;
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
        this.notifyOnChange(KEY.USER_TEMPLATES, this.userTemplates, userTemplates);
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
    private void notifyOnChange(KEY key, Object oldValue, Object newValue) {
        if (!newValue.equals(oldValue)) {
            for (Listener listener : listeners) {
                listener.onChange(key, newValue);
            }
        }
    }

    /**
     * Listener interface for onChange event.
     */
    public interface Listener {
        void onChange(@NotNull KEY key, Object value);
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

    public static class IgnoreLanguagesSettings extends LinkedHashMap<IgnoreLanguage, TreeMap<IgnoreLanguagesSettings.KEY, Object>> {
        public enum KEY {
            NEW_FILE, ENABLE
        }

        /**
         * Returns the value to which the specified key is mapped.
         *
         * @param language Ignore language
         */
        public TreeMap<KEY, Object> get(IgnoreLanguage language) {
            return super.get(language);
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
            for (Map.Entry<IgnoreLanguage, TreeMap<IgnoreLanguagesSettings.KEY, Object>> entry : copy.entrySet()) {
                @SuppressWarnings("unchecked")
                TreeMap<IgnoreLanguagesSettings.KEY, Object> data = (TreeMap<KEY, Object>) entry.getValue().clone();

                copy.put(entry.getKey(), data);
            }
            return copy;
        }
    }
}
