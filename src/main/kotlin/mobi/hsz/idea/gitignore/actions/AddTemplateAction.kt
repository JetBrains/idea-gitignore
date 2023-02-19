// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.Icons
import com.intellij.openapi.vcs.changes.ignore.psi.IgnoreFile as NativeIgnoreFile

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
        val file = e.getData(CommonDataKeys.PSI_FILE) as PsiFile

        GeneratorDialog(project, file).show()
    }

    override fun update(e: AnActionEvent) {
        when (val file = e.getData(CommonDataKeys.PSI_FILE)) {
            is IgnoreFile -> {
                e.presentation.icon = file.fileType.icon
            }

            is NativeIgnoreFile -> {
                e.presentation.icon = Icons.GIT
            }

            else -> {
                e.presentation.isVisible = false
            }
        }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT
}
