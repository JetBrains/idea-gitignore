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

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsConfigurableProvider;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.ui.IgnoreSettingsPanel;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Configuration interface for {@link IgnoreSettings}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
public class IgnoreSettingsConfigurable implements SearchableConfigurable, VcsConfigurableProvider {

    /** The settings storage object. */
    private final IgnoreSettings settings;

    /** The settings UI form. */
    private IgnoreSettingsPanel settingsPanel;

    /** Build a new instance of {@link IgnoreSettingsConfigurable}. */
    public IgnoreSettingsConfigurable() {
        settings = IgnoreSettings.getInstance();
    }

    /**
     * Returns the user-visible name of the settings component.
     *
     * @return the visible name of the component {@link IgnoreSettingsConfigurable}
     */
    @Override
    public String getDisplayName() {
        return IgnoreBundle.message("settings.displayName");
    }

    /**
     * Returns the topic in the help file which is shown when help for the configurable is requested.
     *
     * @return the help topic, or null if no help is available {@link #getDisplayName()}
     */
    @NotNull
    @Override
    public String getHelpTopic() {
        return getDisplayName();
    }

    /**
     * Returns the user interface component for editing the configuration.
     *
     * @return the {@link IgnoreSettingsPanel} component instance
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsPanel == null) settingsPanel = new IgnoreSettingsPanel();
        reset();
        return settingsPanel.panel;
    }

    /**
     * Checks if the settings in the user interface component were modified by the user and need to be saved.
     *
     * @return true if the settings were modified, false otherwise.
     */
    @Override
    public boolean isModified() {
        return settingsPanel == null
                || settingsPanel.missingGitignore == null || settings.isMissingGitignore() != settingsPanel.missingGitignore.isSelected()
                || settingsPanel.templatesListPanel == null || !Utils.equalLists(settings.getUserTemplates(), settingsPanel.templatesListPanel.getList())
                || settingsPanel.ignoredFileStatus == null || settings.isIgnoredFileStatus() != settingsPanel.ignoredFileStatus.isSelected()
                || settingsPanel.outerIgnoreRules == null || settings.isOuterIgnoreRules() != settingsPanel.outerIgnoreRules.isSelected()
                || settingsPanel.languagesTable == null
                    || !((IgnoreSettingsPanel.LanguagesTableModel) settingsPanel.languagesTable.getModel()).equalSettings(settings.getLanguagesSettings())
                ;
    }

    /**
     * Store the settings from configurable to other components.
     */
    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel == null) return;
        settings.setMissingGitignore(settingsPanel.missingGitignore != null && settingsPanel.missingGitignore.isSelected());
        settings.setUserTemplates(settingsPanel.templatesListPanel.getList());
        settings.setIgnoredFileStatus(settingsPanel.ignoredFileStatus != null && settingsPanel.ignoredFileStatus.isSelected());
        settings.setOuterIgnoreRules(settingsPanel.outerIgnoreRules != null && settingsPanel.outerIgnoreRules.isSelected());
        settings.setLanguagesSettings(((IgnoreSettingsPanel.LanguagesTableModel) settingsPanel.languagesTable.getModel()).getSettings());
    }

    /**
     * Load settings from other components to configurable.
     */
    @Override
    public void reset() {
        if (settingsPanel == null) return;
        if (settingsPanel.missingGitignore != null) settingsPanel.missingGitignore.setSelected(settings.isMissingGitignore());
        if (settingsPanel.templatesListPanel != null) settingsPanel.templatesListPanel.resetForm(settings.getUserTemplates());
        if (settingsPanel.ignoredFileStatus != null) settingsPanel.ignoredFileStatus.setSelected(settings.isIgnoredFileStatus());
        if (settingsPanel.outerIgnoreRules != null) settingsPanel.outerIgnoreRules.setSelected(settings.isOuterIgnoreRules());
        if (settingsPanel.languagesTable != null) {
            IgnoreSettingsPanel.LanguagesTableModel model = (IgnoreSettingsPanel.LanguagesTableModel) settingsPanel.languagesTable.getModel();
            model.update(settings.getLanguagesSettings().clone());
        }
    }

    /**
     * Disposes the Swing components used for displaying the configuration.
     */
    @Override
    public void disposeUIResources() {
        settingsPanel.dispose();
        settingsPanel = null;
    }

    @Nullable
    @Override
    public Configurable getConfigurable(Project project) {
        return this;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }
}
