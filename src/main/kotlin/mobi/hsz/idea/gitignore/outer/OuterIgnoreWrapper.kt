// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.outer

import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.gitignore.IgnoreBundle.message
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitExcludeLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Utils.createPreviewEditor
import mobi.hsz.idea.gitignore.util.Utils.openFile
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.ScrollPaneConstants
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

/**
 * Wrapper that creates bottom editor component for displaying outer ignore rules.
 */
class OuterIgnoreWrapper(project: Project, language: IgnoreLanguage, private val outerFiles: List<VirtualFile>) : MouseAdapter(),
    ChangeListener, Disposable {
    
    /** Main wrapper panel. */
    private val panel = JPanel(BorderLayout())

    /** List of outer editors in the wrapper. */
    private val outerEditors = mutableListOf<Editor>()

    /** The settings storage object. */
    private val settings = IgnoreSettings.getInstance()

    /** North panel. */
    private val northPanel: JPanel

    /** Panel wrapper. */
    private val tabbedPanel: TabbedPaneWrapper

    /** Message bus instance. */
    private var messageBus: MessageBusConnection?

    /** Link label instance. */
    private val linkLabel: LinkLabel<*>

    /** Current panel's height. */
    private var dragPanelHeight = 0

    /** Y position of the drag event. */
    private var dragYOnScreen = 0

    /** Obtains if it's in drag mode. */
    private var drag = false

    /** Updates tabbedPanel policy depending on UISettings#getScrollTabLayoutInEditor() settings. */
    private fun updateTabbedPanelPolicy() {
        if (UISettings.instance.scrollTabLayoutInEditor) {
            tabbedPanel.tabLayoutPolicy = JTabbedPane.SCROLL_TAB_LAYOUT
        } else {
            tabbedPanel.tabLayoutPolicy = JTabbedPane.WRAP_TAB_LAYOUT
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (e.point.getY() <= DRAG_OFFSET) {
            dragPanelHeight = tabbedPanel.component.height
            dragYOnScreen = e.yOnScreen
            drag = true
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        drag = false
        settings.setOuterIgnoreWrapperHeight(tabbedPanel.component.height)
    }

    override fun mouseMoved(e: MouseEvent) {
        val cursor = if (e.point.getY() <= DRAG_OFFSET) Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) else Cursor.getDefaultCursor()
        panel.cursor = cursor
    }

    override fun mouseDragged(e: MouseEvent) {
        if (drag) {
            var height = dragPanelHeight - e.yOnScreen + dragYOnScreen
            if (height > MAX_HEIGHT) {
                height = MAX_HEIGHT
            }
            tabbedPanel.component.preferredSize = Dimension(0, height)
            panel.revalidate()
        }
    }

    override fun stateChanged(e: ChangeEvent) {
        linkLabel.text = outerFiles[tabbedPanel.selectedIndex].path
    }

    val component: JComponent
        get() = panel

    override fun dispose() {
        northPanel.removeMouseListener(this)
        northPanel.removeMouseMotionListener(this)
        tabbedPanel.removeChangeListener(this)
        if (messageBus != null) {
            messageBus!!.disconnect()
            messageBus = null
        }
        for (outerEditor in outerEditors) {
            EditorFactory.getInstance().releaseEditor(outerEditor)
        }
    }

    companion object {
        private const val DRAG_OFFSET = 10
        private const val MAX_HEIGHT = 300
    }

    init {
        panel.border = BorderFactory.createEmptyBorder(0, 10, 5, 10)
        northPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))
        val label = JBLabel(
            message("outer.label"),
            UIUtil.ComponentStyle.REGULAR,
            UIUtil.FontColor.BRIGHTER
        )
        label.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        northPanel.add(label)
        tabbedPanel = TabbedPaneWrapper(project)
        messageBus = project.messageBus.connect()
        messageBus!!.subscribe(UISettingsListener.TOPIC, UISettingsListener { uiSettings: UISettings? -> updateTabbedPanelPolicy() })
        updateTabbedPanelPolicy()
        linkLabel = LinkLabel(
            outerFiles[0].path,
            null
        ) { aSource: LinkLabel<*>?, aLinkData: Any? -> openFile(project, outerFiles[tabbedPanel.selectedIndex]) }
        val userHomeDir = VfsUtil.getUserHomeDir()
        for (outerFile in outerFiles) {
            val document = FileDocumentManager.getInstance().getDocument(outerFile)
            val outerEditor = if (document != null) createPreviewEditor(document, null, true) else null
            if (outerEditor != null) {
                val scrollPanel = ScrollPaneFactory.createScrollPane(outerEditor.component)
                scrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                scrollPanel.preferredSize = Dimension(0, settings.getOuterIgnoreWrapperHeight())
                var path = outerFile.path
                if (userHomeDir != null) {
                    path = path.replace(userHomeDir.path, "~")
                }
                val icon = if (language is GitLanguage || language is GitExcludeLanguage) AllIcons.Vcs.Ignore_file else language.icon!!
                tabbedPanel.addTab(path, icon, scrollPanel, outerFile.canonicalPath)
                outerEditors.add(outerEditor)
            }
        }
        northPanel.addMouseListener(this)
        northPanel.addMouseMotionListener(this)
        tabbedPanel.addChangeListener(this)
        panel.add(northPanel, BorderLayout.NORTH)
        panel.add(tabbedPanel.component, BorderLayout.CENTER)
        panel.add(linkLabel, BorderLayout.SOUTH)
    }
}
