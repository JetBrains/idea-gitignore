// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.ide.actions.CloseEditorsActionBase
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.changes.ChangeListManager
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.vcs.IGNORED

/**
 * Action that closes all opened files that are marked as ignored.
 */
class CloseIgnoredEditorsAction : CloseEditorsActionBase() {

    override fun isFileToClose(editor: EditorComposite, window: EditorWindow, fileEditorManager: FileEditorManagerEx): Boolean {
        val project = fileEditorManager.project
        val fileStatusManager = FileStatusManager.getInstance(project) ?: return false
        val changeListManager = ChangeListManager.getInstance(project)
        return editor.file.run {
            fileStatusManager.getStatus(this) == IGNORED || changeListManager.isIgnoredFile(this)
        }
    }

    override fun isActionEnabled(project: Project, event: AnActionEvent) =
        super.isActionEnabled(project, event) && ApplicationManager.getApplication().runReadAction(Computable {
            ProjectLevelVcsManager.getInstance(project).allActiveVcss.isNotEmpty()
        })

    override fun getPresentationText(inSplitter: Boolean) = when {
        inSplitter -> IgnoreBundle.message("action.closeIgnored.editors.in.tab.group")
        else -> IgnoreBundle.message("action.closeIgnored.editors")
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT
}
