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
import com.intellij.openapi.components.service
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
import mobi.hsz.idea.gitignore.util.Resources.Template.Container
import mobi.hsz.idea.gitignore.util.Resources.Template.Container.STARRED
import mobi.hsz.idea.gitignore.util.Resources.Template.Container.USER
import mobi.hsz.idea.gitignore.util.Utils.createPreviewEditor
import mobi.hsz.idea.gitignore.util.Utils.getWords
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
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
@Suppress("MagicNumber", "TooManyFunctions", "UnsafeCallOnNullableType")
class GeneratorDialog(private val project: Project, var file: PsiFile? = null, var action: CreateFileCommandAction? = null) :
    DialogWrapper(project, false) {

    /** Cache set to store checked templates for the current action. */
    private val checked: MutableSet<Resources.Template?> = HashSet()

    /** Settings instance. */
    private val settings = service<IgnoreSettings>()

    /** Set of the starred templates. */
    private val starred = settings.starredTemplates.toMutableSet()

    /** Templates tree root node. */
    private val root = TemplateTreeNode()

    /** Templates tree with checkbox feature. */
    private lateinit var tree: CheckboxTree

    /** Tree expander responsible for expanding and collapsing tree structure. */
    private var treeExpander: DefaultTreeExpander? = null

    /** Dynamic templates filter. */
    private var profileFilter = TemplatesFilterComponent()

    /** [Document] related to the [Editor] feature. */
    private val previewDocument = EditorFactory.getInstance().createDocument("")

    /** Preview editor with syntax highlight. */
    private val preview = createPreviewEditor(previewDocument, project, true)

    /** CheckboxTree selection listener. */
    private val treeSelectionListener = TreeSelectionListener { _: TreeSelectionEvent? ->
        tree.selectionPath?.let { updateDescriptionPanel(it) }
    }

    companion object {
        @NonNls
        private val TEMPLATES_FILTER_HISTORY = "TEMPLATES_FILTER_HISTORY"
        private val STAR = AllIcons.Ide.Rating

        /**
         * Creates or gets existing group node for specified element.
         *
         * @param root      tree root node
         * @param container container type to search
         * @return group node
         */
        private fun getGroupNode(root: TemplateTreeNode, container: Container) =
            root.children().toList().find { it is TemplateTreeNode && it.container == container } as TemplateTreeNode?
                ?: TemplateTreeNode(container).apply { root.add(this) }
    }

    init {
        title = message("dialog.generator.title")
        setOKButtonText(message("global.generate"))
        setCancelButtonText(message("global.cancel"))
        init()
    }

    override fun getPreferredFocusedComponent() = profileFilter

    override fun dispose() {
        tree.removeTreeSelectionListener(treeSelectionListener)
        EditorFactory.getInstance().releaseEditor(preview)
        super.dispose()
    }

    override fun doOKAction() {
        if (isOKActionEnabled) {
            performAppendAction(ignoreDuplicates = false, ignoreComments = false)
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
            iterator.next()?.let {
                content
                    .append(message("file.templateSection", it.name))
                    .append(Constants.NEWLINE)
                    .append(it.content)

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
                    hashSetOf(content.toString()),
                    ignoreDuplicates,
                    ignoreComments
                ).execute()
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
        super.doOKAction()
    }

    override fun createDefaultActions() {
        super.createDefaultActions()
        myOKAction = OptionOkAction()
    }

    override fun createCenterPanel() = JPanel(BorderLayout()).apply {
        preferredSize = Dimension(800, 500)

        // splitter panel - contains tree panel and preview component
        add(
            JBSplitter(false, 0.4f).apply {
                firstComponent = JPanel(BorderLayout()).apply {
                    val treeScrollPanel = createTreeScrollPanel()

                    /* Scroll panel for the templates tree. */
                    add(treeScrollPanel, BorderLayout.CENTER)
                    add(
                        JPanel(GridBagLayout()).apply {
                            border = JBUI.Borders.empty(2, 0)
                            add(
                                createTreeActionsToolbarPanel(treeScrollPanel).component,
                                GridBagConstraints(
                                    0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE_LEADING,
                                    GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
                                )
                            )
                            add(
                                profileFilter,
                                GridBagConstraints(
                                    1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.BASELINE_TRAILING,
                                    GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
                                )
                            )
                        },
                        BorderLayout.NORTH
                    )
                }
                secondComponent = preview.component
            },
            BorderLayout.CENTER
        )
    }

    /**
     * Creates scroll panel with templates tree in it.
     *
     * @return scroll panel
     */
    private fun createTreeScrollPanel(): JScrollPane {
        fillTreeData(null, true)

        val renderer = object : TemplateTreeRenderer() {
            override val filter
                get() = profileFilter.filter
        }

        tree = object : CheckboxTree(renderer, root) {
            override fun getPreferredScrollableViewportSize(): Dimension {
                val size = super.getPreferredScrollableViewportSize()
                return Dimension(size.width + 10, size.height)
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
        val actions = DefaultActionGroup().apply {
            add(actionManager.createExpandAllAction(treeExpander, tree))
            add(actionManager.createCollapseAllAction(treeExpander, tree))
            add(
                object : AnAction(message("dialog.generator.unselectAll"), null, AllIcons.Actions.Unselectall) {
                    override fun update(e: AnActionEvent) {
                        e.presentation.isEnabled = checked.isNotEmpty()
                    }

                    override fun actionPerformed(e: AnActionEvent) {
                        checked.clear()
                        filterTree(profileFilter.textEditor.text)
                    }
                }
            )
            add(
                object : AnAction(message("dialog.generator.star"), null, STAR) {
                    override fun update(e: AnActionEvent) {
                        val node = currentNode
                        val disabled = node == null || USER == node.container || !node.isLeaf
                        val unstar = node != null && STARRED == node.container
                        e.presentation.apply {
                            isEnabled = !disabled
                            icon = when {
                                disabled -> IconLoader.getDisabledIcon(STAR)
                                unstar -> IconLoader.getTransparentIcon(STAR)
                                else -> STAR
                            }
                            text = message(if (unstar) "dialog.generator.unstar" else "dialog.generator.star")
                        }
                    }

                    override fun actionPerformed(e: AnActionEvent) {
                        currentNode?.template?.let {
                            it.isStarred = !it.isStarred
                            if (it.isStarred) {
                                starred.add(it.name)
                            } else {
                                starred.remove(it.name)
                            }
                            settings.starredTemplates = starred.toList()
                            refreshTree()
                        }
                    }

                    /**
                     * Returns current [TemplateTreeNode] node if available.
                     *
                     * @return current node
                     */
                    private val currentNode
                        get() = tree.selectionPath?.lastPathComponent as TemplateTreeNode?
                }
            )
        }

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
                previewDocument.replaceString(0, previewDocument.textLength, content)
                highlightWords(getFilterRanges(profileFilter.textEditor.text, content))
            }
        }
    }

    /**
     * Fills templates tree with templates fetched with `Resources.getGitignoreTemplates`.
     *
     * @param filter       templates filter
     * @param forceInclude force include
     */
    private fun fillTreeData(filter: String?, forceInclude: Boolean) {
        root.removeAllChildren()
        root.isChecked = false
        Container.values().forEach {
            root.add(
                TemplateTreeNode(it).apply {
                    isChecked = false
                }
            )
        }
        Resources.gitignoreTemplates.forEach {
            if (isTemplateAccepted(it, filter)) {
                getGroupNode(root, it.container).add(
                    TemplateTreeNode(it).apply {
                        isChecked = checked.contains(it)
                    }
                )
            }
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
    private fun getFilterRanges(filter: String, content: String) = mutableListOf<Pair<Int, Int>>().apply {
        getWords(filter).forEach {
            var index = content.indexOf(it, ignoreCase = true)
            while (index >= 0) {
                add(Pair.create(index, index + it.length))
                index = content.indexOf(it, index + 1, ignoreCase = true)
            }
        }
    }

    /**
     * Checks if given template is accepted by passed filter.
     *
     * @param template to check
     * @param filter   templates filter
     * @return template is accepted
     */
    private fun isTemplateAccepted(template: Resources.Template, filter: String?) =
        filter.isNullOrEmpty() ||
            template.name.contains(filter, true) ||
            !getWords(filter).any { !template.name.contains(it, true) } ||
            getFilterRanges(filter, StringUtil.notNullize(template.content)).isNotEmpty()

    /**
     * Filters templates tree.
     *
     * @param filter text
     */
    private fun filterTree(filter: String?) {
        fillTreeData(filter, true)
        reloadModel()
        TreeUtil.expandAll(tree)
        if (tree.selectionPath == null) {
            TreeUtil.selectFirstNode(tree)
        }
    }

    /** Refreshes current tree. */
    private fun refreshTree() {
        filterTree(profileFilter.textEditor.text)
    }

    /**
     * Highlights given text ranges in [.preview] content.
     *
     * @param pairs text ranges
     */
    private fun highlightWords(pairs: List<Pair<Int, Int>>) {
        val attr = TextAttributes().apply {
            backgroundColor = UIUtil.getTreeSelectionBackground(true)
            foregroundColor = UIUtil.getTreeSelectionForeground(true)
        }
        pairs.forEach { pair ->
            preview.markupModel.addRangeHighlighter(
                pair.first,
                pair.second,
                0,
                attr,
                HighlighterTargetArea.EXACT_RANGE
            )
        }
    }

    /** Reloads tree model. */
    private fun reloadModel() {
        (tree.model as DefaultTreeModel).reload()
    }

    inner class TemplatesFilterComponent : FilterComponent(TEMPLATES_FILTER_HISTORY, 10) {
        override fun filter() {
            filterTree(filter)
        }
    }

    /** OkAction instance with additional `Generate without duplicates` action. */
    private inner class OptionOkAction : OkAction(), OptionAction {
        override fun getOptions(): Array<Action> {
            return arrayOf(
                object : DialogWrapperAction(message("global.generate.without.duplicates")) {
                    override fun doAction(e: ActionEvent) {
                        performAppendAction(ignoreDuplicates = true, ignoreComments = false)
                    }
                },
                object : DialogWrapperAction(message("global.generate.without.comments")) {
                    override fun doAction(e: ActionEvent) {
                        performAppendAction(ignoreDuplicates = false, ignoreComments = true)
                    }
                }
            )
        }
    }
}
