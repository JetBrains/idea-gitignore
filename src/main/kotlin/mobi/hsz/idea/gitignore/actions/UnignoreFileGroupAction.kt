// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

/**
 * Group action that unignores specified file or directory.
 * ActionGroup expands single action into a more child options to allow user specify
 * the IgnoreFile that will be used for file's path storage.
 */
class UnignoreFileGroupAction : IgnoreFileGroupAction(
    "action.addToUnignore.group",
    "action.addToUnignore.group.description",
    "action.addToUnignore.group.noPopup"
) {

    private val settings = service<IgnoreSettings>()

    /**
     * Creates new [UnignoreFileAction] action instance.
     *
     * @param file current file
     * @return action instance
     */
    override fun createAction(file: VirtualFile) = UnignoreFileAction(file)

    /**
     * Presents a list of suitable Gitignore files that can cover currently selected [VirtualFile].
     * Shows a subgroup with available files or one option if only one Gitignore file is available.
     * Group will be hidden if [IgnoreSettings.unignoreActions] is set to `false`.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val status = settings.unignoreActions
        e.presentation.isVisible = status
        if (status) {
            super.update(e)
        }
    }
}
