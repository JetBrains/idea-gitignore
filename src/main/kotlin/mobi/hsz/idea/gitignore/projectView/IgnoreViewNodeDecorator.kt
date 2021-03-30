// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

/**
 * [ProjectViewNodeDecorator] implementation to show on the Project Tree if ignored file is still tracked with Git.
 */
class IgnoreViewNodeDecorator(project: Project) : ProjectViewNodeDecorator {

    private val manager = project.service<IgnoreManager>()
    private val ignoreSettings = service<IgnoreSettings>()

    companion object {
        private val GRAYED_SMALL_ATTRIBUTES = SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER, UIUtil.getInactiveTextColor())
    }

    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val file = node.virtualFile ?: return
        if (ignoreSettings.hideIgnoredFiles && file.isDirectory) {
            val count = file.children.filter { manager.isFileIgnored(it) }.size
            if (count > 0) {
                addColoredText(data, IgnoreBundle.message("projectView.containsHidden", count))
            }
        }
    }

    override fun decorate(node: PackageDependenciesNode, cellRenderer: ColoredTreeCellRenderer) = Unit

    /**
     * Adds ColoredFragment to the node's presentation.
     *
     * @param data node's presentation data
     * @param text text to add
     */
    private fun addColoredText(data: PresentationData, text: String) {
        if (data.coloredText.isEmpty()) {
            data.addText(data.presentableText, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        data.addText(" $text", GRAYED_SMALL_ATTRIBUTES)
    }
}
