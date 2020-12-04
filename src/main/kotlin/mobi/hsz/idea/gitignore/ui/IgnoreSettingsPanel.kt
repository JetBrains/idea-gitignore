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
    private var missingGitignoreCheckBox: JCheckBox? = null

    /** Templates list panel. */
    private var templatesListPanel: TemplatesListPanel? = null

    /** Enable ignored file status coloring. */
    private var ignoredFileStatusCheckBox: JCheckBox? = null

    /** Defines if new content should be inserted at the cursor's position or at the document end. */
    private var insertAtCursorCheckBox: JCheckBox? = null

    /** Splitter element. */
    private var templatesSplitter: Splitter? = null

    /** File types scroll panel with table. */
    private var languagesPanel: JScrollPane? = null

    /** Settings table. */
    private var languagesTable: JBTable? = null

    /** Enable unignore files group. */
    private var unignoreFiles: JCheckBox? = null

    /** Inform about editing ignored file. */
    private var notifyIgnoredEditingCheckBox: JCheckBox? = null

    /** Editor panel element. */
    private var editorPanel: EditorPanel? = null

    private fun createUIComponents() {
        templatesListPanel = TemplatesListPanel()
        editorPanel = EditorPanel()
        editorPanel!!.preferredSize = Dimension(Int.MAX_VALUE, 200)
        templatesSplitter = Splitter(false, 0.3f)
        templatesSplitter!!.firstComponent = templatesListPanel
        templatesSplitter!!.secondComponent = editorPanel
        languagesTable = JBTable()
        languagesTable!!.model = LanguagesTableModel()
        languagesTable!!.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        languagesTable!!.columnSelectionAllowed = false
        languagesTable!!.rowHeight = 22
        languagesTable!!.columnModel.getColumn(2).cellRenderer = object : BooleanTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable,
                value: Any,
                isSel: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                val editable = table.isCellEditable(row, column)
                val newValue = if (editable) value else null
                return super.getTableCellRendererComponent(table, newValue, isSel, hasFocus, row, column)
            }
        }
        languagesTable!!.preferredScrollableViewportSize = Dimension(-1, languagesTable!!.rowHeight * IgnoreBundle.LANGUAGES.size / 2)
        languagesTable!!.isStriped = true
        languagesTable!!.setShowGrid(false)
        languagesTable!!.border = JBUI.Borders.empty()
        languagesTable!!.dragEnabled = false
        languagesPanel = ScrollPaneFactory.createScrollPane(languagesTable)
    }

    override fun dispose() {
        if (!editorPanel!!.preview.isDisposed) {
            EditorFactory.getInstance().releaseEditor(editorPanel!!.preview)
        }
    }

    var missingGitignore
        get() = missingGitignoreCheckBox!!.isSelected
        set(selected) {
            missingGitignoreCheckBox!!.isSelected = selected
        }

    var ignoredFileStatus
        get() = ignoredFileStatusCheckBox!!.isSelected
        set(selected) {
            ignoredFileStatusCheckBox!!.isSelected = selected
        }

    var userTemplates: List<UserTemplate>
        get() = templatesListPanel!!.list
        set(userTemplates) {
            templatesListPanel!!.resetForm(userTemplates)
        }

    var insertAtCursor
        get() = insertAtCursorCheckBox!!.isSelected
        set(selected) {
            insertAtCursorCheckBox!!.isSelected = selected
        }

    var unignoreActions
        get() = unignoreFiles!!.isSelected
        set(selected) {
            unignoreFiles!!.isSelected = selected
        }

    var notifyIgnoredEditing
        get() = notifyIgnoredEditingCheckBox!!.isSelected
        set(selected) {
            notifyIgnoredEditingCheckBox!!.isSelected = selected
        }

    val languagesSettings: LanguagesTableModel
        get() = languagesTable!!.model as LanguagesTableModel

    /** Extension for the CRUD list panel. */
    open inner class TemplatesListPanel : AddEditDeleteListPanel<UserTemplate>(null, ArrayList()) {

        override fun customizeDecorator(decorator: ToolbarDecorator) {
            super.customizeDecorator(decorator)
            val group = DefaultActionGroup()
            group.addSeparator()
            group.add(
                object : AnAction(
                    message("action.importTemplates"),
                    message("action.importTemplates.description"),
                    AllIcons.Actions.Install
                ) {
                    override fun actionPerformed(event: AnActionEvent) {
                        val descriptor: FileChooserDescriptor = object : FileChooserDescriptor(true, false, true, false, true, false) {
                            override fun isFileVisible(file: VirtualFile, showHiddenFiles: Boolean): Boolean {
                                return super.isFileVisible(file, showHiddenFiles) &&
                                    (file.isDirectory || file.extension == "xml" || file.fileType === FileTypes.ARCHIVE)
                            }

                            override fun isFileSelectable(file: VirtualFile) = file.fileType === XmlFileType.INSTANCE
                        }
                        descriptor.description = message("action.importTemplates.wrapper.description")
                        descriptor.title = message("action.importTemplates.wrapper")
                        descriptor.putUserData(
                            LangDataKeys.MODULE_CONTEXT,
                            LangDataKeys.MODULE.getData(event.dataContext)
                        )
                        val file = FileChooser.chooseFile(descriptor, templatesListPanel, null, null)
                        if (file != null) {
                            try {
                                val element = JDOMUtil.load(file.inputStream)
                                val templates = IgnoreSettings.loadTemplates(element)
                                for (template in templates) {
                                    myListModel.addElement(template)
                                }
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
                        Messages.showErrorDialog(templatesListPanel!!, message("action.importTemplates.error"))
                    }
                }
            )
            group.add(
                object : AnAction(
                    message("action.exportTemplates"),
                    message("action.exportTemplates.description"),
                    AllIcons.ToolbarDecorator.Export
                ) {
                    override fun actionPerformed(event: AnActionEvent) {
                        val wrapper = FileChooserFactory.getInstance().createSaveFileDialog(
                            FileSaverDescriptor(
                                message("action.exportTemplates.wrapper"),
                                "",
                                "xml"
                            ),
                            templatesListPanel!!
                        ).save(null as VirtualFile?, null)
                        if (wrapper != null) {
                            val items = currentItems
                            val document = org.jdom.Document(
                                IgnoreSettings.createTemplatesElement(items)
                            )
                            try {
                                JDOMUtil.writeDocument(document, wrapper.file, Constants.NEWLINE)
                                Messages.showInfoMessage(
                                    templatesListPanel,
                                    message("action.exportTemplates.success", items.size),
                                    message("action.exportTemplates.success.title")
                                )
                            } catch (e: IOException) {
                                Messages.showErrorDialog(
                                    templatesListPanel!!,
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
                initialValue.name = name
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
            get() = (0 until myListModel.size()).map { myListModel.getElementAt(it)!! }

        /**
         * Updates editor component with given content.
         *
         * @param content new content
         */
        fun updateContent(content: String?) {
            currentItem?.let {
                it.content = content!!
            }
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
                editorPanel!!.isEnabled = enabled
                if (enabled) {
                    editorPanel!!.setContent(currentItem?.content ?: "")
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
                remove(preview.component)
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
            preview = createPreviewEditor(previewDocument, null, false)
            preview.document.addDocumentListener(
                object : DocumentListener {
                    override fun documentChanged(event: DocumentEvent) {
                        templatesListPanel!!.updateContent(event.document.text)
                    }
                }
            )
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

        private val columnClasses = arrayOf<Class<*>>(
            String::class.java,
            Boolean::class.java,
            Boolean::class.java
        )

        override fun getRowCount() = settings.size

        override fun getColumnCount() = columnNames.size

        override fun getColumnName(column: Int) = columnNames[column]

        override fun getColumnClass(columnIndex: Int) = columnClasses[columnIndex]

        override fun isCellEditable(row: Int, column: Int): Boolean {
            val language = ArrayList(settings.keys)[row]
            return if (language != null && column == 2) {
            @Suppress("ForbiddenComment")
            // TODO: if (language != null && column == 2 && IgnoreBundle.isExcludedFromHighlighting(language)) {
                false
            } else column != 0
        }

        override fun getValueAt(row: Int, column: Int): Any {
            val language = ArrayList(settings.keys)[row] ?: return false
            val data = settings[language]
            return when (column) {
                0 -> language.id
                1 -> getBoolean(IgnoreLanguagesSettings.KEY.NEW_FILE, data)
                2 -> getBoolean(IgnoreLanguagesSettings.KEY.ENABLE, data)
                else -> throw IllegalArgumentException()
            }
        }

        private fun getBoolean(key: IgnoreLanguagesSettings.KEY, data: TreeMap<IgnoreLanguagesSettings.KEY, Any>?): Boolean {
            val objectByKey = data!![key] ?: return false
            return java.lang.Boolean.valueOf(objectByKey.toString())
        }

        override fun setValueAt(value: Any, row: Int, column: Int) {
            val language = ArrayList(settings.keys)[row]!!
            val data = settings[language]
            when (column) {
                1 -> {
                    data!![IgnoreLanguagesSettings.KEY.NEW_FILE] = value
                    return
                }
                2 -> {
                    data!![IgnoreLanguagesSettings.KEY.ENABLE] = value
                    return
                }
            }
            throw IllegalArgumentException()
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
