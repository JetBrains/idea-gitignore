// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.CommonActionsManager
import com.intellij.ide.DefaultTreeExpander
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.OptionAction
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.ui.CheckboxTree
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.FilterComponent
import com.intellij.ui.JBSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.tree.TreeUtil
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.ui.template.TemplateTreeComparator
import mobi.hsz.idea.gitignore.ui.template.TemplateTreeNode
import mobi.hsz.idea.gitignore.ui.template.TemplateTreeRenderer
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Resources
import mobi.hsz.idea.gitignore.util.Utils.createPreviewEditor
import mobi.hsz.idea.gitignore.util.Utils.getWords
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.util.ArrayList
import java.util.HashSet
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/**
 * [GeneratorDialog] responsible for displaying list of all available templates and adding selected ones to the specified file.
 */
class GeneratorDialog(private val project: Project, var file: PsiFile? = null, var action: CreateFileCommandAction? = null) :
    DialogWrapper(project, false) {

    /** Cache set to store checked templates for the current action.  */
    private val checked: MutableSet<Resources.Template?> = HashSet()

    /** Set of the starred templates.  */
    private val starred: MutableSet<String> = HashSet()

    /** Settings instance.  */
    private val settings = IgnoreSettings.getInstance()

    /** Templates tree root node.  */
    private val root = TemplateTreeNode()

    /** Templates tree with checkbox feature.  */
    private var tree: CheckboxTree? = null

    /** Tree expander responsible for expanding and collapsing tree structure.  */
    private var treeExpander: DefaultTreeExpander? = null

    /** Dynamic templates filter.  */
    private var profileFilter: FilterComponent? = null

    /** Preview editor with syntax highlight.  */
    private var preview: Editor? = null

    /** [Document] related to the [Editor] feature.  */
    private var previewDocument: Document? = null

    /** CheckboxTree selection listener.  */
    private val treeSelectionListener = TreeSelectionListener { e: TreeSelectionEvent? ->
        tree?.selectionPath?.let { updateDescriptionPanel(it) }
    }

    companion object {
        /** [FilterComponent] search history key.  */
        @NonNls
        private val TEMPLATES_FILTER_HISTORY = "TEMPLATES_FILTER_HISTORY"

        /** Star icon for the favorites action.  */
        private val STAR = AllIcons.Ide.Rating

        /**
         * Creates or gets existing group node for specified element.
         *
         * @param root      tree root node
         * @param container container type to search
         * @return group node
         */
        private fun getGroupNode(root: TemplateTreeNode, container: Resources.Template.Container): TemplateTreeNode {
            val childCount = root.childCount
            (0 until childCount).forEach {
                val child = root.getChildAt(it) as TemplateTreeNode
                if (container == child.container) {
                    return child
                }
            }
            return TemplateTreeNode(container).apply {
                root.add(this)
            }
        }
    }

    init {
        title = message("dialog.generator.title")
        setOKButtonText(message("global.generate"))
        setCancelButtonText(message("global.cancel"))
        init()
    }

    /**
     * Returns component which should be focused when the dialog appears on the screen.
     *
     * @return component to focus
     */
    override fun getPreferredFocusedComponent() = profileFilter

    /**
     * Dispose the wrapped and releases all resources allocated be the wrapper to help
     * more efficient garbage collection. You should never invoke this method twice or
     * invoke any method of the wrapper after invocation of `dispose`.
     *
     * @throws IllegalStateException if the dialog is disposed not on the event dispatch thread
     */
    override fun dispose() {
        tree!!.removeTreeSelectionListener(treeSelectionListener)
        EditorFactory.getInstance().releaseEditor(preview!!)
        super.dispose()
    }

    /**
     * Show the dialog.
     *
     * @throws IllegalStateException if the method is invoked not on the event dispatch thread
     * @see .showAndGet
     */
    override fun show() {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            dispose()
            return
        }
        super.show()
    }

    /**
     * This method is invoked by default implementation of "OK" action. It just closes dialog
     * with `OK_EXIT_CODE`. This is convenient place to override functionality of "OK" action.
     * Note that the method does nothing if "OK" action isn't enabled.
     */
    override fun doOKAction() {
        if (isOKActionEnabled) {
            performAppendAction(false, false)
        }
    }

    /**
     * Performs [AppendFileCommandAction] action.
     *
     * @param ignoreDuplicates ignores duplicated rules
     * @param ignoreComments   ignores comments and empty lines
     */
    private fun performAppendAction(ignoreDuplicates: Boolean, ignoreComments: Boolean) {
        val content = StringBuilder()
        val iterator: Iterator<Resources.Template?> = checked.iterator()
        while (iterator.hasNext()) {
            val template = iterator.next()
            if (template != null) {
                content.append(message("file.templateSection", template.name))
                content.append(Constants.NEWLINE).append(template.content)
                if (iterator.hasNext()) {
                    content.append(Constants.NEWLINE)
                }
            }
        }
        try {
            if (file == null && action != null) {
                file = action!!.execute()
            }
            if (file != null && content.isNotEmpty()) {
                AppendFileCommandAction(
                    project,
                    file!!,
                    ContainerUtil.newHashSet(content.toString()),
                    ignoreDuplicates,
                    ignoreComments
                ).execute()
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
        super.doOKAction()
    }

    /** Creates default actions with appended [OptionOkAction] instance.  */
    override fun createDefaultActions() {
        super.createDefaultActions()
        myOKAction = OptionOkAction()
    }

    /**
     * Factory method. It creates panel with dialog options. Options panel is located at the
     * center of the dialog's content pane. The implementation can return `null`
     * value. In this case there will be no options panel.
     *
     * @return center panel
     */
    override fun createCenterPanel(): JComponent? {
        // general panel
        val centerPanel = JPanel(BorderLayout())
        centerPanel.preferredSize = Dimension(800, 500)

        // splitter panel - contains tree panel and preview component
        val splitter = JBSplitter(false, 0.4f)
        centerPanel.add(splitter, BorderLayout.CENTER)
        val treePanel = JPanel(BorderLayout())
        previewDocument = EditorFactory.getInstance().createDocument("")
        preview = createPreviewEditor(previewDocument!!, project, true)
        splitter.setFirstComponent(treePanel)
        splitter.setSecondComponent(preview!!.component)

        /* Scroll panel for the templates tree. */
        val treeScrollPanel = createTreeScrollPanel()
        treePanel.add(treeScrollPanel, BorderLayout.CENTER)
        val northPanel = JPanel(GridBagLayout())
        northPanel.border = JBUI.Borders.empty(2, 0)
        northPanel.add(
            createTreeActionsToolbarPanel(treeScrollPanel).component,
            GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE_LEADING,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
            )
        )
        northPanel.add(
            profileFilter,
            GridBagConstraints(
                1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE_TRAILING,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
            )
        )
        treePanel.add(northPanel, BorderLayout.NORTH)
        return centerPanel
    }

    /**
     * Creates scroll panel with templates tree in it.
     *
     * @return scroll panel
     */
    private fun createTreeScrollPanel(): JScrollPane {
        fillTreeData(null, true)

        val renderer = object : TemplateTreeRenderer() {
            override val filter: String?
                get() = profileFilter?.filter
        }

        tree = object : CheckboxTree(renderer, root) {
            override fun getPreferredScrollableViewportSize(): Dimension {
                var size = super.getPreferredScrollableViewportSize()
                size = Dimension(size.width + 10, size.height)
                return size
            }

            override fun onNodeStateChanged(node: CheckedTreeNode) {
                super.onNodeStateChanged(node)
                val template = (node as TemplateTreeNode).template
                if (node.isChecked()) {
                    checked.add(template)
                } else {
                    checked.remove(template)
                }
            }
        }.apply {
            cellRenderer = renderer
            isRootVisible = false
            showsRootHandles = true
            addTreeSelectionListener(treeSelectionListener)
            TreeUtil.installActions(this)
            TreeUtil.expandAll(this)

            treeExpander = DefaultTreeExpander(this)
            profileFilter = TemplatesFilterComponent()
        }

        return ScrollPaneFactory.createScrollPane(tree).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
    }

    /**
     * Creates tree toolbar panel with actions for working with templates tree.
     *
     * @param target templates tree
     * @return action toolbar
     */
    private fun createTreeActionsToolbarPanel(target: JComponent): ActionToolbar {
        val actionManager = CommonActionsManager.getInstance()
        val actions = DefaultActionGroup()
        actions.add(actionManager.createExpandAllAction(treeExpander, tree))
        actions.add(actionManager.createCollapseAllAction(treeExpander, tree))
        actions.add(
            object : AnAction(
                message("dialog.generator.unselectAll"),
                null,
                AllIcons.Actions.Unselectall
            ) {
                override fun update(e: AnActionEvent) {
                    e.presentation.isEnabled = !checked.isEmpty()
                }

                override fun actionPerformed(e: AnActionEvent) {
                    checked.clear()
                    filterTree(profileFilter!!.textEditor.text)
                }
            }
        )
        actions.add(
            object : AnAction(message("dialog.generator.star"), null, STAR) {
                override fun update(e: AnActionEvent) {
                    val node = currentNode
                    val disabled = node == null || Resources.Template.Container.USER == node.container || !node.isLeaf
                    val unstar = node != null && Resources.Template.Container.STARRED == node.container
                    val icon = if (disabled) IconLoader.getDisabledIcon(STAR) else if (unstar) IconLoader.getTransparentIcon(STAR) else STAR
                    val text = message(if (unstar) "dialog.generator.unstar" else "dialog.generator.star")
                    val presentation = e.presentation
                    presentation.isEnabled = !disabled
                    presentation.icon = icon
                    presentation.text = text
                }

                override fun actionPerformed(e: AnActionEvent) {
                    val node = currentNode ?: return
                    val template = node.template
                    if (template != null) {
                        val isStarred = !template.isStarred
                        template.isStarred = isStarred
                        refreshTree()
                        if (isStarred) {
                            starred.add(template.name)
                        } else {
                            starred.remove(template.name)
                        }
                        settings.starredTemplates = ArrayList(starred)
                    }
                }

                /**
                 * Returns current [TemplateTreeNode] node if available.
                 *
                 * @return current node
                 */
                private val currentNode
                    get() = tree?.selectionPath?.lastPathComponent as TemplateTreeNode?
            }
        )

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true).apply {
            setTargetComponent(target)
        }
    }

    /**
     * Updates editor's content depending on the selected [TreePath].
     *
     * @param path selected tree path
     */
    private fun updateDescriptionPanel(path: TreePath) {
        val node = path.lastPathComponent as TemplateTreeNode
        val template = node.template
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction {
                val content = if (template != null) StringUtil.notNullize(template.content).replace('\r', '\u0000') else ""
                previewDocument!!.replaceString(0, previewDocument!!.textLength, content)
                val pairs = getFilterRanges(profileFilter!!.textEditor.text, content)
                highlightWords(pairs)
            }
        }
    }

    /**
     * Fills templates tree with templates fetched with [Resources.getGitignoreTemplates].
     *
     * @param filter       templates filter
     * @param forceInclude force include
     */
    private fun fillTreeData(filter: String?, forceInclude: Boolean) {
        root.removeAllChildren()
        root.isChecked = false
        for (container in Resources.Template.Container.values()) {
            val node = TemplateTreeNode(container)
            node.isChecked = false
            root.add(node)
        }
        val templatesList = Resources.gitignoreTemplates
        for (template in templatesList) {
            if (filter != null && filter.isNotEmpty() && !isTemplateAccepted(template, filter)) {
                continue
            }
            val node = TemplateTreeNode(template)
            node.isChecked = checked.contains(template)
            getGroupNode(root, template.container).add(node)
        }
        if (filter != null && forceInclude && root.childCount == 0) {
            fillTreeData(filter, false)
        }
        TreeUtil.sort(root, TemplateTreeComparator())
    }

    /**
     * Finds for the filter's words in the given content and returns their positions.
     *
     * @param filter  templates filter
     * @param content templates content
     * @return text ranges
     */
    private fun getFilterRanges(filter: String, content: String): List<Pair<Int, Int>> {
        var content = content
        val pairs: MutableList<Pair<Int, Int>> = ArrayList()
        content = content.toLowerCase()
        for (word in getWords(filter)) {
            var index = content.indexOf(word)
            while (index >= 0) {
                pairs.add(Pair.create(index, index + word.length))
                index = content.indexOf(word, index + 1)
            }
        }
        return pairs
    }

    /**
     * Checks if given template is accepted by passed filter.
     *
     * @param template to check
     * @param filter   templates filter
     * @return template is accepted
     */
    private fun isTemplateAccepted(template: Resources.Template, filter: String): Boolean {
        var filter = filter
        filter = filter.toLowerCase()
        if (StringUtil.containsIgnoreCase(template.name, filter)) {
            return true
        }
        var nameAccepted = true
        for (word in getWords(filter)) {
            if (!StringUtil.containsIgnoreCase(template.name, word)) {
                nameAccepted = false
                break
            }
        }
        val ranges = getFilterRanges(filter, StringUtil.notNullize(template.content))
        return nameAccepted || ranges.size > 0
    }

    /**
     * Filters templates tree.
     *
     * @param filter text
     */
    private fun filterTree(filter: String?) {
        if (tree != null) {
            fillTreeData(filter, true)
            reloadModel()
            TreeUtil.expandAll(tree!!)
            if (tree!!.selectionPath == null) {
                TreeUtil.selectFirstNode(tree!!)
            }
        }
    }

    /** Refreshes current tree.  */
    private fun refreshTree() {
        filterTree(profileFilter!!.textEditor.text)
    }

    /**
     * Highlights given text ranges in [.preview] content.
     *
     * @param pairs text ranges
     */
    private fun highlightWords(pairs: List<Pair<Int, Int>>) {
        val attr = TextAttributes()
        attr.backgroundColor = UIUtil.getTreeSelectionBackground(true)
        attr.foregroundColor = UIUtil.getTreeSelectionForeground(true)
        for (pair in pairs) {
            preview!!.markupModel.addRangeHighlighter(
                pair.first,
                pair.second,
                0,
                attr,
                HighlighterTargetArea.EXACT_RANGE
            )
        }
    }

    /** Reloads tree model.  */
    private fun reloadModel() {
        (tree!!.model as DefaultTreeModel).reload()
    }

    /** Custom templates [FilterComponent].  */
    private inner class TemplatesFilterComponent
    /** Builds a new instance of [TemplatesFilterComponent].  */
        : FilterComponent(TEMPLATES_FILTER_HISTORY, 10) {
        /** Filters tree using current filter's value.  */
        override fun filter() {
            filterTree(filter)
        }
    }

    /** [OkAction] instance with additional `Generate without duplicates` action.  */
    private inner class OptionOkAction : OkAction(), OptionAction {
        override fun getOptions(): Array<Action> {
            return arrayOf(
                object : DialogWrapperAction(message("global.generate.without.duplicates")) {
                    override fun doAction(e: ActionEvent) {
                        performAppendAction(true, false)
                    }
                },
                object : DialogWrapperAction(message("global.generate.without.comments")) {
                    override fun doAction(e: ActionEvent) {
                        performAppendAction(false, true)
                    }
                }
            )
        }
    }
}
