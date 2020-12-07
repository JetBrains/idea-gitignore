// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Creates new file or returns existing one.
 */
open class NewFileAction(private val fileType: IgnoreFileType) : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val view = e.getRequiredData(LangDataKeys.IDE_VIEW)
        val directory = view.orChooseDirectory ?: return

        val filename = fileType.ignoreLanguage.filename
        var file = directory.findFile(filename)
        val virtualFile = file?.virtualFile ?: directory.virtualFile.findChild(filename)
        val dialog =
            if (file == null || virtualFile == null) {
                GeneratorDialog(project, action = CreateFileCommandAction(project, directory, fileType))
            } else {
                Notifications.Bus.notify(
                    Notification(
                        fileType.languageName,
                        IgnoreBundle.message("action.newFile.exists", fileType.languageName),
                        @Suppress("DialogTitleCapitalization")
                        IgnoreBundle.message("action.newFile.exists.in", virtualFile.path),
                        NotificationType.INFORMATION
                    ),
                    project
                )
                file = Utils.getPsiFile(project, virtualFile)
                GeneratorDialog(project, file)
            }.apply {
                show()
            }

        file = dialog.file
        if (file != null) {
            Utils.openFile(project, file)
        }
    }

    @Suppress("ComplexCondition")
    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val view = e.getData(LangDataKeys.IDE_VIEW)
        val directory = view?.directories
        if (directory == null || directory.isEmpty() || project == null || !fileType.ignoreLanguage.isNewAllowed) {
            e.presentation.isVisible = false
        }
    }
}
