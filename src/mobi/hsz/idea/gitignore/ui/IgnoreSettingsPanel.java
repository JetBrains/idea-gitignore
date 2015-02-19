/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AddEditDeleteListPanel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.gitignore.GitignoreFileType;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UI form for {@link IgnoreSettings} edition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
public class IgnoreSettingsPanel implements Disposable {

    /** The parent panel for the form. */
    public JPanel panel;

    /** Form element for {@link mobi.hsz.idea.gitignore.settings.IgnoreSettings#missingGitignore}. */
    public JCheckBox missingGitignore;

    /** Templates list panel. */
    public TemplatesListPanel templatesListPanel;

    /** Splitter element. */
    private Splitter templatesSplitter;

    /** Editor panel element. */
    private EditorPanel editorPanel;

    /** Create UI components. */
    private void createUIComponents() {
        templatesListPanel = new TemplatesListPanel();
        editorPanel = new EditorPanel();

        templatesSplitter = new Splitter(false, 0.3f) {
            @Override
            public Dimension getMinimumSize() {
                Dimension dimension = super.getMinimumSize();
                dimension.height = 300;
                return dimension;
            }
        };
        templatesSplitter.setFirstComponent(templatesListPanel);
        templatesSplitter.setSecondComponent(editorPanel);
    }

    /**
     * Creates and configures template preview editor.
     *
     * @param document virtual editor document
     * @return editor
     */
    @NotNull
    private static Editor createPreviewEditor(@NotNull Document document) {
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(document, null, GitignoreFileType.INSTANCE, false);
        final EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(false);
        settings.setAdditionalLinesCount(1);
        settings.setAdditionalColumnsCount(1);
        settings.setRightMarginShown(false);
        settings.setFoldingOutlineShown(false);
        settings.setLineMarkerAreaShown(false);
        settings.setIndentGuidesShown(false);
        settings.setVirtualSpace(false);
        settings.setWheelFontChangeEnabled(false);

        EditorColorsScheme colorsScheme = editor.getColorsScheme();
        colorsScheme.setColor(EditorColors.CARET_ROW_COLOR, null);
        return editor;
    }

    @Override
    public void dispose() {
        EditorFactory.getInstance().releaseEditor(editorPanel.preview);
    }

    /**
     * Extension for the CRUD list panel.
     */
    public class TemplatesListPanel extends AddEditDeleteListPanel<IgnoreSettings.UserTemplate> {

        /** Constructs CRUD panel with list listener for editor updating. */
        public TemplatesListPanel() {
            super(null, new ArrayList<IgnoreSettings.UserTemplate>());
            myList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    boolean enabled = myListModel.size() > 0;
                    editorPanel.setEnabled(enabled);

                    if (enabled) {
                        IgnoreSettings.UserTemplate template = getCurrentItem();
                        editorPanel.setContent(template != null ? template.getContent() : "");
                    }
                }
            });
        }

        /**
         * Opens edit dialog for new template.
         *
         * @return template
         */
        @Nullable
        @Override
        protected IgnoreSettings.UserTemplate findItemToAdd() {
            return showEditDialog(new IgnoreSettings.UserTemplate());
        }

        /**
         * SHows edit dialog and validates user's input name.
         *
         * @param initialValue template
         * @return modified template
         */
        @Nullable
        private IgnoreSettings.UserTemplate showEditDialog(@NotNull final IgnoreSettings.UserTemplate initialValue) {
            String name = Messages.showInputDialog(this,
                    IgnoreBundle.message("settings.userTemplates.dialogDescription"),
                    IgnoreBundle.message("settings.userTemplates.dialogTitle"),
                    Messages.getQuestionIcon(), initialValue.getName(), new InputValidatorEx() {

                /**
                 * Checks whether the <code>inputString</code> is valid. It is invoked each time
                 * input changes.
                 *
                 * @param inputString the input to check
                 * @return true if input string is valid
                 */
                @Override
                public boolean checkInput(String inputString) {
                    return !StringUtil.isEmpty(inputString);
                }

                /**
                 * This method is invoked just before message dialog is closed with OK code.
                 * If <code>false</code> is returned then then the message dialog will not be closed.
                 *
                 * @param inputString the input to check
                 * @return true if the dialog could be closed, false otherwise.
                 */
                @Override
                public boolean canClose(String inputString) {
                    return !StringUtil.isEmpty(inputString);
                }

                /**
                 * Returns error message depending on the input string.
                 *
                 * @param inputString the input to check
                 * @return error text
                 */
                @Nullable
                @Override
                public String getErrorText(String inputString) {
                    if (!checkInput(inputString)) {
                        return IgnoreBundle.message("settings.userTemplates.dialogError");
                    }
                    return null;
                }
            });

            if (name != null) {
                initialValue.setName(name);
            }
            return initialValue.isEmpty() ? null : initialValue;
        }

        /**
         * Fills list element with given templates list.
         *
         * @param userTemplates templates list
         */
        public void resetFrom(List<IgnoreSettings.UserTemplate> userTemplates) {
            myListModel.clear();
            for (IgnoreSettings.UserTemplate template : userTemplates) {
                myListModel.addElement(new IgnoreSettings.UserTemplate(template.getName(), template.getContent()));
            }
        }

        /**
         * Edits given template.
         *
         * @param item template
         * @return modified template
         */
        @Override
        protected IgnoreSettings.UserTemplate editSelectedItem(IgnoreSettings.UserTemplate item) {
            return showEditDialog(item);
        }

        /**
         * Returns current templates list.
         *
         * @return templates list
         */
        public List<IgnoreSettings.UserTemplate> getList() {
            ArrayList<IgnoreSettings.UserTemplate> list = new ArrayList<IgnoreSettings.UserTemplate>();
            for (int i = 0; i < myListModel.size(); i++) {
                list.add((IgnoreSettings.UserTemplate) myListModel.getElementAt(i));
            }
            return list;
        }

        /**
         * Updates editor component with given content.
         *
         * @param content new content
         */
        public void updateContent(String content) {
            IgnoreSettings.UserTemplate template = getCurrentItem();
            if (template != null) {
                template.setContent(content);
            }
        }

        /**
         * Returns currently selected template.
         *
         * @return template or null if none selected
         */
        @Nullable
        public IgnoreSettings.UserTemplate getCurrentItem() {
            int index = myList.getSelectedIndex();
            if (index == -1) {
                return null;
            }
            return (IgnoreSettings.UserTemplate) myListModel.get(index);
        }
    }

    /**
     * Editor panel class that displays document editor or label if no template is selected.
     */
    private class EditorPanel extends JBPanel {
        private final Editor preview;
        private final JBLabel label;
        private final Document previewDocument;

        /**
         * Constructor that creates document editor, empty content label.
         */
        public EditorPanel() {
            super(new BorderLayout());
            this.previewDocument = EditorFactory.getInstance().createDocument("");
            this.label = new JBLabel(IgnoreBundle.message("settings.userTemplates.noTemplateSelected"), JBLabel.CENTER);
            this.preview = createPreviewEditor(previewDocument);
            this.preview.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void beforeDocumentChange(DocumentEvent event) {
                }

                @Override
                public void documentChanged(DocumentEvent event) {
                    templatesListPanel.updateContent(event.getDocument().getText());
                }
            });

            setEnabled(false);
        }

        /**
         * Shows or hides label and editor.
         *
         * @param enabled if true shows editor, else shows label
         */
        public void setEnabled(boolean enabled) {
            if (enabled) {
                remove(this.label);
                add(this.preview.getComponent());
            } else {
                add(this.label);
                remove(this.preview.getComponent());
            }
            revalidate();
            repaint();
        }

        /**
         * Sets new content to the editor component.
         *
         * @param content new content
         */
        public void setContent(@NotNull final String content) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                        @Override
                        public void run() {
                            previewDocument.replaceString(0, previewDocument.getTextLength(), content);
                        }
                    });
                }
            });
        }


    }
}
