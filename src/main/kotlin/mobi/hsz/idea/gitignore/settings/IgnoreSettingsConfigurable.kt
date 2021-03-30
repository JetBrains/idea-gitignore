// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.VcsConfigurableProvider
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.ui.IgnoreSettingsPanel
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Configuration interface for [IgnoreSettings].
 */
@Suppress("UnsafeCallOnNullableType")
class IgnoreSettingsConfigurable : SearchableConfigurable, VcsConfigurableProvider {

    private val settings = service<IgnoreSettings>()
    private var settingsPanel = IgnoreSettingsPanel()

    override fun getDisplayName(): String = IgnoreBundle.message("settings.displayName")

    override fun getHelpTopic(): String = displayName

    override fun createComponent() = settingsPanel.panel

    override fun isModified() = settingsPanel.run {
        !Comparing.equal(settings.missingGitignore, missingGitignore) ||
            !Utils.equalLists(settings.userTemplates, userTemplates) ||
            !Comparing.equal(settings.ignoredFileStatus, ignoredFileStatus) ||
            !Comparing.equal(settings.insertAtCursor, insertAtCursor) ||
            !Comparing.equal(settings.unignoreActions, unignoreActions) ||
            !Comparing.equal(settings.notifyIgnoredEditing, notifyIgnoredEditing) ||
            !languagesSettings.equalSettings(settings.languagesSettings)
    }

    override fun apply() {
        settingsPanel.apply {
            settings.missingGitignore = missingGitignore
            settings.userTemplates = userTemplates.toMutableList()
            settings.ignoredFileStatus = ignoredFileStatus
            settings.insertAtCursor = insertAtCursor
            settings.languagesSettings = languagesSettings.settings
            settings.unignoreActions = unignoreActions
            settings.notifyIgnoredEditing = notifyIgnoredEditing
        }
    }

    override fun reset() {
        settingsPanel.apply {
            missingGitignore = settings.missingGitignore
            userTemplates = settings.userTemplates
            ignoredFileStatus = settings.ignoredFileStatus
            insertAtCursor = settings.insertAtCursor
            unignoreActions = settings.unignoreActions
            notifyIgnoredEditing = settings.notifyIgnoredEditing
            languagesSettings.update(settings.languagesSettings.clone())
        }
    }

    override fun disposeUIResources() {
        Disposer.dispose(settingsPanel)
    }

    override fun getConfigurable(project: Project) = this

    override fun getId() = helpTopic

    override fun enableSearch(option: String): Runnable? = null
}
