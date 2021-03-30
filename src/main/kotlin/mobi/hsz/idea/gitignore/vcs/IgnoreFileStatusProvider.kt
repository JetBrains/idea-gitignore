// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.vcs

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusFactory
import com.intellij.openapi.vcs.impl.FileStatusProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager

/**
 * Ignore instance of [FileStatusProvider] that provides [.IGNORED] status for the files matched with ignore rules.
 */
class IgnoreFileStatusProvider(project: Project) : FileStatusProvider, DumbAware {

    private val manager = project.service<IgnoreManager>()

    companion object {
        /** Ignored status.  */
        val IGNORED: FileStatus = FileStatusFactory.getInstance().createFileStatus(
            "IGNORE.PROJECT_VIEW.IGNORED",
            IgnoreBundle.message("projectView.ignored"),
            JBColor.GRAY
        )
    }

    /**
     * Returns the [.IGNORED] status if file is ignored or `null`.
     *
     * @param virtualFile file to check
     * @return [.IGNORED] status or `null`
     */
    override fun getFileStatus(virtualFile: VirtualFile) = when {
        manager.isFileIgnored(virtualFile) -> IGNORED
        else -> null
    }
}
