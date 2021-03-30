// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template

import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.settings.IgnoreSettings.UserTemplate
import mobi.hsz.idea.gitignore.util.Notify.show
import mobi.hsz.idea.gitignore.util.Utils.createPreviewEditor
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * User template dialog that allows user to add custom templates.
 */
@Suppress("MagicNumber", "UnsafeCallOnNullableType")
class UserTemplateDialog(private val project: Project, private val content: String) : DialogWrapper(project, false) {

    private val settings = service<IgnoreSettings>()
    private var preview: Editor? = null
    private var previewDocument: Document? = null
    private var name: JBTextField? = null

    init {
        title = message("dialog.userTemplate.title")
        setOKButtonText(message("global.create"))
        setCancelButtonText(message("global.cancel"))
        init()
    }

    override fun createCenterPanel(): JComponent {
        val centerPanel = JPanel(BorderLayout())
        centerPanel.preferredSize = Dimension(600, 300)
        previewDocument = EditorFactory.getInstance().createDocument(content)
        preview = createPreviewEditor(previewDocument!!, project, false)
        name = JBTextField(message("dialog.userTemplate.name.value"))
        val nameLabel = JLabel(message("dialog.userTemplate.name"))
        nameLabel.border = JBUI.Borders.emptyRight(10)
        val namePanel = JPanel(BorderLayout())
        namePanel.add(nameLabel, BorderLayout.WEST)
        namePanel.add(name, BorderLayout.CENTER)
        val previewComponent = preview!!.component
        previewComponent.border = JBUI.Borders.emptyTop(10)
        centerPanel.add(namePanel, BorderLayout.NORTH)
        centerPanel.add(previewComponent, BorderLayout.CENTER)
        return centerPanel
    }

    override fun getPreferredFocusedComponent() = name

    override fun dispose() {
        EditorFactory.getInstance().releaseEditor(preview!!)
        super.dispose()
    }

    override fun doOKAction() {
        if (isOKActionEnabled) {
            performCreateAction()
        }
    }

    /**
     * Creates new user template.
     */
    private fun performCreateAction() {
        val template = UserTemplate(name!!.text, previewDocument!!.text)
        settings.userTemplates.add(template)
        show(
            project,
            message("dialog.userTemplate.added"),
            message("dialog.userTemplate.added.description", template.name),
            NotificationType.INFORMATION
        )
        super.doOKAction()
    }
}
