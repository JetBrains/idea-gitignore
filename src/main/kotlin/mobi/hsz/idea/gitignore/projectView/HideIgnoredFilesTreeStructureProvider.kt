// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.projectView

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.BasePsiNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.VcsIgnoreManager
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

/**
 * Extension for the [TreeStructureProvider] that provides the ability to hide ignored files
 * or directories in the project tree view.
 */
class HideIgnoredFilesTreeStructureProvider(project: Project) : TreeStructureProvider {

    private val ignoreSettings = service<IgnoreSettings>()
    private val manager = project.service<IgnoreManager>()

    /**
     * If [IgnoreSettings.hideIgnoredFiles] is set to `true`, checks if specific
     * nodes are ignored and filters them out.
     *
     * @param parent   the parent node
     * @param children the list of child nodes according to the default project structure
     * @param settings the current project view settings
     * @return the modified collection of child nodes
     */
    override fun modify(parent: AbstractTreeNode<*>, children: Collection<AbstractTreeNode<*>?>, settings: ViewSettings?) =
        when {
            !ignoreSettings.hideIgnoredFiles -> children
            else -> children.filter {
                if (it !is BasePsiNode<*>) {
                    return@filter true
                }
                val file = it.virtualFile

                return@filter file != null && !manager.isFileIgnored(file)
            }
        }

    override fun getData(collection: Collection<AbstractTreeNode<*>?>, s: String): Any? = null
}
