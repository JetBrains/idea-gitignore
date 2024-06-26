// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.text.StringUtil
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.ui.template.UserTemplateDialog
import mobi.hsz.idea.gitignore.util.Icons
import com.intellij.openapi.vcs.changes.ignore.psi.IgnoreFile as NativeIgnoreFile

/**
 * Action that creates new user template with predefined content - i.e. from currently opened file.
 */
internal class CreateUserTemplateAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (file !is IgnoreFile && file !is NativeIgnoreFile) {
            return
        }
        var content = file.text
        file.viewProvider.document?.let { document ->
            EditorFactory.getInstance().getEditors(document).first()?.let { editor ->
                val selectedText = editor.selectionModel.selectedText
                if (!StringUtil.isEmpty(selectedText)) {
                    content = selectedText
                }
            }
        }
        UserTemplateDialog(project, content).show()
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
