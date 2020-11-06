// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.projectView

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.BasePsiNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

/**
 * Extension for the [TreeStructureProvider] that provides the ability to hide ignored files
 * or directories in the project tree view.
 */
class HideIgnoredFilesTreeStructureProvider(project: Project) : TreeStructureProvider {

    private val ignoreSettings = IgnoreSettings.getInstance()
    private val ignoreManager = IgnoreManager.getInstance(project)
    private val changeListManager = ChangeListManager.getInstance(project)

    /**
     * If [IgnoreSettings.hideIgnoredFiles] is set to `true`, checks if specific
     * nodes are ignored and filters them out.
     *
     * @param parent   the parent node
     * @param children the list of child nodes according to the default project structure
     * @param settings the current project view settings
     * @return the modified collection of child nodes
     */
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>?>,
        settings: ViewSettings?
    ): Collection<AbstractTreeNode<*>?> =
        if (!ignoreSettings.hideIgnoredFiles || children.isEmpty()) {
            children
        } else {
            ContainerUtil.filter(children) { node: AbstractTreeNode<*>? ->
                if (node is BasePsiNode<*>) {
                    val file = node.virtualFile
                    return@filter file != null && (!changeListManager.isIgnoredFile(file) &&
                        !ignoreManager.isFileIgnored(file) || ignoreManager.isFileTracked(file))
                }
                true
            }
        }

    override fun getData(collection: Collection<AbstractTreeNode<*>?>, s: String): Any? = null

}
