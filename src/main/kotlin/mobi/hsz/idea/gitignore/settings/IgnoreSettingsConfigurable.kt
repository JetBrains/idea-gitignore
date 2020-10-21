// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.vcs.VcsConfigurableProvider
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.ui.IgnoreSettingsPanel
import mobi.hsz.idea.gitignore.util.Utils
import javax.swing.JComponent

/**
 * Configuration interface for [IgnoreSettings].
 */
class IgnoreSettingsConfigurable : SearchableConfigurable, VcsConfigurableProvider {

    /** The settings storage object.  */
    private val settings = IgnoreSettings.getInstance()

    /** The settings UI form.  */
    private var settingsPanel: IgnoreSettingsPanel? = null

    /**
     * Returns the user-visible name of the settings component.
     *
     * @return the visible name of the component [IgnoreSettingsConfigurable]
     */
    override fun getDisplayName(): String = IgnoreBundle.message("settings.displayName")

    /**
     * Returns the topic in the help file which is shown when help for the configurable is requested.
     *
     * @return the help topic, or null if no help is available [.getDisplayName]
     */
    override fun getHelpTopic(): String = displayName

    /**
     * Returns the user interface component for editing the configuration.
     *
     * @return the [IgnoreSettingsPanel] component instance
     */
    override fun createComponent(): JComponent? {
        if (settingsPanel == null) {
            settingsPanel = IgnoreSettingsPanel()
        }
        reset()
        return settingsPanel!!.panel
    }

    /**
     * Checks if the settings in the user interface component were modified by the user and need to be saved.
     *
     * @return true if the settings were modified, false otherwise.
     */
    override fun isModified() = settingsPanel!!.run {
        !Comparing.equal(settings.missingGitignore, isMissingGitignore) ||
            !Utils.equalLists(settings.userTemplates, userTemplates) ||
            !Comparing.equal(settings.ignoredFileStatus, isIgnoredFileStatus) ||
            !Comparing.equal(settings.outerIgnoreRules, isOuterIgnoreRules) ||
            !Comparing.equal(settings.insertAtCursor, isInsertAtCursor) ||
            !Comparing.equal(settings.addUnversionedFiles, isAddUnversionedFiles) ||
            !Comparing.equal(settings.unignoreActions, isUnignoreActions) ||
            !Comparing.equal(settings.notifyIgnoredEditing, isNotifyIgnoredEditing) ||
            !languagesSettings.equalSettings(settings.languagesSettings)
    }

    /** Store the settings from configurable to other components. */
    override fun apply() {
        settingsPanel!!.apply {
            settings.missingGitignore = isMissingGitignore
            settings.userTemplates = userTemplates
            settings.ignoredFileStatus = isIgnoredFileStatus
            settings.outerIgnoreRules = isOuterIgnoreRules
            settings.insertAtCursor = isInsertAtCursor
            settings.addUnversionedFiles = isAddUnversionedFiles
            settings.languagesSettings = languagesSettings.settings
            settings.unignoreActions = isUnignoreActions
            settings.notifyIgnoredEditing = isNotifyIgnoredEditing
        }
    }

    /** Load settings from other components to configurable. */
    override fun reset() {
        settingsPanel!!.apply {
            isMissingGitignore = settings.missingGitignore
            userTemplates = settings.userTemplates
            isIgnoredFileStatus = settings.ignoredFileStatus
            isOuterIgnoreRules = settings.outerIgnoreRules
            isInsertAtCursor = settings.insertAtCursor
            isAddUnversionedFiles = settings.addUnversionedFiles
            isUnignoreActions = settings.unignoreActions
            isNotifyIgnoredEditing = settings.notifyIgnoredEditing
            languagesSettings.update(settings.languagesSettings.clone())
        }
    }

    /** Disposes the Swing components used for displaying the configuration. */
    override fun disposeUIResources() {
        settingsPanel!!.dispose()
        settingsPanel = null
    }

    /**
     * Returns current [Configurable] instance.
     *
     * @param project ignored
     * @return current instance
     */
    override fun getConfigurable(project: Project) = this

    /**
     * Returns help topic as an ID.
     *
     * @return id
     */
    override fun getId() = helpTopic

    /**
     * An action to perform when this configurable is opened.
     *
     * @param option setting search query
     * @return null
     */
    override fun enableSearch(option: String): Runnable? = null
}
