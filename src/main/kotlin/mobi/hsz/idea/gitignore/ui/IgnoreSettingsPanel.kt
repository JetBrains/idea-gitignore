// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.AddEditDeleteListPanel
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.settings.IgnoreSettings.IgnoreLanguagesSettings
import mobi.hsz.idea.gitignore.settings.IgnoreSettings.UserTemplate
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Utils.createPreviewEditor
import org.jdom.Document
import org.jdom.JDOMException
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.io.IOException
import java.util.ArrayList
import java.util.TreeMap
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

/**
 * UI form for [IgnoreSettings] edition.
 */
@Suppress("MagicNumber")
class IgnoreSettingsPanel : Disposable {

    /** The parent panel for the form. */
    var panel: JPanel? = null

    /** Form element for IgnoreSettings#missingGitignore. */
    private lateinit var missingGitignoreCheckBox: JCheckBox

    /** Templates list panel. */
    private lateinit var templatesListPanel: TemplatesListPanel

    /** Enable ignored file status coloring. */
    private lateinit var ignoredFileStatusCheckBox: JCheckBox

    /** Defines if new content should be inserted at the cursor's position or at the document end. */
    private lateinit var insertAtCursorCheckBox: JCheckBox

    /** Splitter element. */
    private lateinit var templatesSplitter: Splitter

    /** File types scroll panel with table. */
    private lateinit var languagesPanel: JScrollPane

    /** Settings table. */
    private lateinit var languagesTable: JBTable

    /** Enable unignore files group. */
    private lateinit var unignoreFiles: JCheckBox

    /** Inform about editing ignored file. */
    private lateinit var notifyIgnoredEditingCheckBox: JCheckBox

    /** Editor panel element. */
    private lateinit var editorPanel: EditorPanel

    companion object {
        const val NAME_COLUMN = 0
        const val NEW_FILE_COLUMN = 1
        const val ENABLE_COLUMN = 2
    }

    @Suppress("UnusedPrivateMember")
    private fun createUIComponents() {
        templatesListPanel = TemplatesListPanel()
        editorPanel = EditorPanel().apply {
            preferredSize = Dimension(Int.MAX_VALUE, 200)
        }
        templatesSplitter = Splitter(false, 0.3f).apply {
            firstComponent = templatesListPanel
            secondComponent = editorPanel
        }
        languagesTable = JBTable().apply {
            model = LanguagesTableModel()
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
            columnSelectionAllowed = false
            rowHeight = 22
            columnModel.getColumn(NEW_FILE_COLUMN).apply {
                cellEditor = BooleanTableCellEditor()
                cellRenderer = BooleanTableCellRenderer()
            }
            columnModel.getColumn(ENABLE_COLUMN).apply {
                cellEditor = BooleanTableCellEditor()
                cellRenderer = object : BooleanTableCellRenderer() {
                    override fun getTableCellRendererComponent(
                        table: JTable,
                        value: Any,
                        isSel: Boolean,
                        hasFocus: Boolean,
                        row: Int,
                        column: Int,
                    ): Component {
                        val editable = table.isCellEditable(row, column)
                        val newValue = if (editable) value else null
                        return super.getTableCellRendererComponent(table, newValue, isSel, hasFocus, row, column)
                    }
                }
            }
            preferredScrollableViewportSize = Dimension(-1, rowHeight * IgnoreBundle.LANGUAGES.size / 2)
            isStriped = true
            border = JBUI.Borders.empty()
            dragEnabled = false
            setShowGrid(false)
        }
        languagesPanel = ScrollPaneFactory.createScrollPane(languagesTable)
    }

    override fun dispose() {
        if (!editorPanel.preview.isDisposed) {
            EditorFactory.getInstance().releaseEditor(editorPanel.preview)
        }
    }

    var missingGitignore
        get() = missingGitignoreCheckBox.isSelected
        set(selected) {
            missingGitignoreCheckBox.isSelected = selected
        }

    var ignoredFileStatus
        get() = ignoredFileStatusCheckBox.isSelected
        set(selected) {
            ignoredFileStatusCheckBox.isSelected = selected
        }

    var userTemplates: List<UserTemplate>
        get() = templatesListPanel.list
        set(userTemplates) {
            templatesListPanel.resetForm(userTemplates)
        }

    var insertAtCursor
        get() = insertAtCursorCheckBox.isSelected
        set(selected) {
            insertAtCursorCheckBox.isSelected = selected
        }

    var unignoreActions
        get() = unignoreFiles.isSelected
        set(selected) {
            unignoreFiles.isSelected = selected
        }

    var notifyIgnoredEditing
        get() = notifyIgnoredEditingCheckBox.isSelected
        set(selected) {
            notifyIgnoredEditingCheckBox.isSelected = selected
        }

    val languagesSettings: LanguagesTableModel
        get() = languagesTable.model as LanguagesTableModel

    /** Extension for the CRUD list panel. */
    open inner class TemplatesListPanel : AddEditDeleteListPanel<UserTemplate>(null, ArrayList()) {

        override fun customizeDecorator(decorator: ToolbarDecorator) {
            super.customizeDecorator(decorator)
            val group = DefaultActionGroup().apply {
                addSeparator()
                add(
                    object : AnAction(
                        message("action.importTemplates"),
                        message("action.importTemplates.description"),
                        AllIcons.Actions.Install
                    ) {
                        override fun actionPerformed(event: AnActionEvent) {
                            val descriptor: FileChooserDescriptor = object : FileChooserDescriptor(true, false, true, false, true, false) {
                                override fun isFileVisible(file: VirtualFile, showHiddenFiles: Boolean) =
                                    super.isFileVisible(file, showHiddenFiles) &&
                                        (file.isDirectory || file.extension == "xml" || file.fileType === FileTypes.ARCHIVE)

                                override fun isFileSelectable(file: VirtualFile) = file.fileType === XmlFileType.INSTANCE
                            }.apply {
                                description = message("action.importTemplates.wrapper.description")
                                title = message("action.importTemplates.wrapper")
                                putUserData(
                                    LangDataKeys.MODULE_CONTEXT,
                                    LangDataKeys.MODULE.getData(event.dataContext)
                                )
                            }

                            FileChooser.chooseFile(descriptor, templatesListPanel, null, null)?.let { file ->
                                try {
                                    val element = JDOMUtil.load(file.inputStream)
                                    val templates = IgnoreSettings.loadTemplates(element)
                                    templates.forEach { myListModel.addElement(it) }
                                    Messages.showInfoMessage(
                                        templatesListPanel,
                                        message("action.importTemplates.success", templates.size),
                                        message("action.exportTemplates.success.title")
                                    )
                                    return
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                } catch (e: JDOMException) {
                                    e.printStackTrace()
                                }
                            }
                            Messages.showErrorDialog(templatesListPanel, message("action.importTemplates.error"))
                        }
                    }
                )
                add(
                    object : AnAction(
                        message("action.exportTemplates"),
                        message("action.exportTemplates.description"),
                        AllIcons.ToolbarDecorator.Export
                    ) {
                        override fun actionPerformed(event: AnActionEvent) {
                            FileChooserFactory.getInstance().createSaveFileDialog(
                                FileSaverDescriptor(
                                    message("action.exportTemplates.wrapper"),
                                    "",
                                    "xml"
                                ),
                                templatesListPanel
                            ).save(null as VirtualFile?, null)?.let { wrapper ->
                                val items = currentItems
                                val document = Document(IgnoreSettings.createTemplatesElement(items))
                                try {
                                    JDOMUtil.writeDocument(document, wrapper.file, Constants.NEWLINE)
                                    Messages.showInfoMessage(
                                        templatesListPanel,
                                        message("action.exportTemplates.success", items.size),
                                        message("action.exportTemplates.success.title")
                                    )
                                } catch (e: IOException) {
                                    Messages.showErrorDialog(
                                        templatesListPanel,
                                        message("action.exportTemplates.error")
                                    )
                                }
                            }
                        }

                        override fun update(e: AnActionEvent) {
                            e.presentation.isEnabled = currentItems.isNotEmpty()
                        }
                    }
                )
            }
            decorator.setActionGroup(group)
        }

        override fun findItemToAdd() = showEditDialog(UserTemplate())

        private fun showEditDialog(initialValue: UserTemplate): UserTemplate? {
            Messages.showInputDialog(
                this,
                message("settings.userTemplates.dialogDescription"),
                message("settings.userTemplates.dialogTitle"),
                Messages.getQuestionIcon(),
                initialValue.name,
                object : InputValidatorEx {
                    override fun checkInput(inputString: String) = !StringUtil.isEmpty(inputString)

                    override fun canClose(inputString: String) = !StringUtil.isEmpty(inputString)

                    override fun getErrorText(inputString: String) =
                        message("settings.userTemplates.dialogError").takeUnless { checkInput(inputString) }
                }
            )?.let {
                initialValue.name = it
            }

            return initialValue.takeUnless { initialValue.isEmpty }
        }

        fun resetForm(userTemplates: List<UserTemplate>) {
            myListModel.clear()
            userTemplates.forEach { (name, content) ->
                myListModel.addElement(UserTemplate(name, content))
            }
        }

        override fun editSelectedItem(item: UserTemplate) = showEditDialog(item)

        val list
            get() = myListModel.elements().toList()

        /**
         * Updates editor component with given content.
         *
         * @param content new content
         */
        fun updateContent(content: String?) {
            currentItem?.content = content ?: ""
        }

        private val currentItem: UserTemplate?
            get() {
                val index = myList.selectedIndex
                return if (index == -1) {
                    null
                } else myListModel[index]
            }

        /**
         * Returns selected [IgnoreSettings.UserTemplate] elements.
         *
         * @return [IgnoreSettings.UserTemplate] list
         */
        val currentItems
            get() = myList.selectedIndices.indices.map { list[it] }

        /** Constructs CRUD panel with list listener for editor updating. */
        init {
            myList.addListSelectionListener {
                val enabled = myListModel.size() > 0
                editorPanel.isEnabled = enabled
                if (enabled) {
                    editorPanel.setContent(currentItem?.content ?: "")
                }
            }
        }
    }

    /** Editor panel class that displays document editor or label if no template is selected. */
    private inner class EditorPanel : JPanel(BorderLayout()) {
        /** Preview editor. */
        val preview: Editor

        /** `No templates is selected` label. */
        private val label = JBLabel(message("settings.userTemplates.noTemplateSelected"), JBLabel.CENTER)

        /** Preview document. */
        private val previewDocument = EditorFactory.getInstance().createDocument("")

        /**
         * Shows or hides label and editor.
         *
         * @param enabled if true shows editor, else shows label
         */
        override fun setEnabled(enabled: Boolean) {
            if (enabled) {
                remove(label)
                add(preview.component)
            } else {
                add(label)
                remove(preview!!.component)
            }
            revalidate()
            repaint()
        }

        /**
         * Sets new content to the editor component.
         *
         * @param content new content
         */
        fun setContent(content: String) {
            ApplicationManager.getApplication().runWriteAction {
                CommandProcessor.getInstance()
                    .runUndoTransparentAction { previewDocument.replaceString(0, previewDocument.textLength, content) }
            }
        }

        /** Constructor that creates document editor, empty content label. */
        init {
            preview = createPreviewEditor(previewDocument, null, false).apply {
                document.addDocumentListener(
                    object : DocumentListener {
                        override fun documentChanged(event: DocumentEvent) {
                            templatesListPanel.updateContent(event.document.text)
                        }
                    }
                )
            }
            isEnabled = false
        }
    }

    /** Languages table helper class. */
    class LanguagesTableModel : AbstractTableModel() {
        val settings = IgnoreLanguagesSettings()

        private val columnNames = arrayOf(
            message("settings.languagesSettings.table.name"),
            message("settings.languagesSettings.table.newFile"),
            message("settings.languagesSettings.table.enable")
        )

        private val columnClasses = arrayOf(
            String::class.java,
            Boolean::class.java,
            Boolean::class.java
        )

        override fun getRowCount() = settings.size

        override fun getColumnCount() = columnNames.size

        override fun getColumnName(column: Int) = columnNames[column]

        override fun getColumnClass(columnIndex: Int) = columnClasses[columnIndex]

        override fun isCellEditable(row: Int, column: Int) =
            column > 0 || (column == 2 && settings.keys.toList()[row]?.let(IgnoreBundle::isExcludedFromHighlighting) ?: false)

        override fun getValueAt(row: Int, column: Int): Any {
            val language = settings.keys.toList()[row] ?: return false
            val data = settings[language]
            return when (column) {
                NAME_COLUMN -> language.id
                NEW_FILE_COLUMN -> getBoolean(IgnoreLanguagesSettings.KEY.NEW_FILE, data)
                ENABLE_COLUMN -> getBoolean(IgnoreLanguagesSettings.KEY.ENABLE, data)
                else -> throw IllegalArgumentException("Unknown column name: $column")
            }
        }

        private fun getBoolean(key: IgnoreLanguagesSettings.KEY, data: TreeMap<IgnoreLanguagesSettings.KEY, Any>?) =
            data?.get(key).toString().toBoolean()

        override fun setValueAt(value: Any, row: Int, column: Int) {
            val language = settings.keys.toList()[row]
            val data = settings[language]
            when (column) {
                NEW_FILE_COLUMN -> {
                    data?.set(IgnoreLanguagesSettings.KEY.NEW_FILE, value)
                    return
                }
                ENABLE_COLUMN -> {
                    data?.set(IgnoreLanguagesSettings.KEY.ENABLE, value)
                    return
                }
            }
            throw IllegalArgumentException("Unknown column name: $column")
        }

        fun update(settings: IgnoreLanguagesSettings) {
            this.settings.apply {
                clear()
                putAll(settings)
            }
        }

        fun equalSettings(settings: IgnoreLanguagesSettings) = this.settings == settings
    }
}
