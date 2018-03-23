/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.ui.template;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * User template dialog that allows user to add custom templates.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public class UserTemplateDialog extends DialogWrapper {
    /** Current working project. */
    @NotNull
    private final Project project;

    /** Initial content. */
    @NotNull
    private final String content;

    /** Settings instance. */
    @NotNull
    private final IgnoreSettings settings;

    /** Preview editor with syntax highlight. */
    private Editor preview;

    /** {@link Document} related to the {@link Editor} feature. */
    private Document previewDocument;

    /** Name field. element */
    private JBTextField name;

    /**
     * Builds a new instance of {@link UserTemplateDialog}.
     *
     * @param project current working project
     */
    public UserTemplateDialog(@NotNull Project project, @NotNull String content) {
        super(project, false);
        this.project = project;
        this.content = content;
        this.settings = IgnoreSettings.getInstance();

        setTitle(IgnoreBundle.message("dialog.userTemplate.title"));
        setOKButtonText(IgnoreBundle.message("global.create"));
        setCancelButtonText(IgnoreBundle.message("global.cancel"));
        init();
    }

    /**
     * Factory method. It creates panel with dialog options. Options panel is located at the
     * center of the dialog's content pane. The implementation can return <code>null</code>
     * value. In this case there will be no options panel.
     *
     * @return center panel
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        final JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(600, 300));

        previewDocument = EditorFactory.getInstance().createDocument(content);
        preview = Utils.createPreviewEditor(previewDocument, project, false);
        name = new JBTextField(IgnoreBundle.message("dialog.userTemplate.name.value"));

        JLabel nameLabel = new JLabel(IgnoreBundle.message("dialog.userTemplate.name"));
        nameLabel.setBorder(IdeBorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(name, BorderLayout.CENTER);

        JComponent previewComponent = preview.getComponent();
        previewComponent.setBorder(IdeBorderFactory.createEmptyBorder(10, 0, 0, 0));

        centerPanel.add(namePanel, BorderLayout.NORTH);
        centerPanel.add(previewComponent, BorderLayout.CENTER);

        return centerPanel;
    }

    /**
     * Returns component which should be focused when the dialog appears on the screen.
     *
     * @return component to focus
     */
    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return name;
    }

    /**
     * Dispose the wrapped and releases all resources allocated be the wrapper to help
     * more efficient garbage collection. You should never invoke this method twice or
     * invoke any method of the wrapper after invocation of <code>dispose</code>.
     *
     * @throws IllegalStateException if the dialog is disposed not on the event dispatch thread
     */
    @Override
    protected void dispose() {
        EditorFactory.getInstance().releaseEditor(preview);
        super.dispose();
    }

    /**
     * This method is invoked by default implementation of "OK" action. It just closes dialog
     * with <code>OK_EXIT_CODE</code>. This is convenient place to override functionality of "OK" action.
     * Note that the method does nothing if "OK" action isn't enabled.
     */
    @Override
    protected void doOKAction() {
        if (isOKActionEnabled()) {
            performCreateAction();
        }
    }

    /**
     * Creates new user template.
     */
    private void performCreateAction() {
        IgnoreSettings.UserTemplate template =
                new IgnoreSettings.UserTemplate(name.getText(), previewDocument.getText());
        settings.getUserTemplates().add(template);

        Notifications.Bus.notify(new Notification(
                IgnoreBundle.PLUGIN_ID,
                IgnoreBundle.message("dialog.userTemplate.added"),
                IgnoreBundle.message("dialog.userTemplate.added.description", template.getName()),
                NotificationType.INFORMATION
        ), project);

        super.doOKAction();
    }
}
