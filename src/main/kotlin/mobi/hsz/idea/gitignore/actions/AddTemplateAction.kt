// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.ui.GeneratorDialog

/**
 * Action that initiates adding new template to the selected .gitignore file.
 */
class AddTemplateAction : AnAction(
    IgnoreBundle.message("action.addTemplate"),
    IgnoreBundle.message("action.addTemplate.description"),
    null,
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as IgnoreFile

        GeneratorDialog(project, file).show()
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (file !is IgnoreFile) {
            e.presentation.isVisible = false
            return
        }
        templatePresentation.icon = file.fileType.icon
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
