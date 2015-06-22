/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.AddEditDeleteListPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mobi.hsz.idea.gitignore.settings.IgnoreSettings.IgnoreLanguagesSettings.KEY.ENABLE;
import static mobi.hsz.idea.gitignore.settings.IgnoreSettings.IgnoreLanguagesSettings.KEY.NEW_FILE;


/**
 * UI form for {@link IgnoreSettings} edition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
public class IgnoreSettingsPanel implements Disposable {
    /**
     * The parent panel for the form.
     */
    public JPanel panel;

    /**
     * Form element for {@link IgnoreSettings#missingGitignore}.
     */
    public JCheckBox missingGitignore;

    /**
     * Templates list panel.
     */
    public TemplatesListPanel templatesListPanel;

    /**
     * Enable ignored file status coloring.
     */
    public JCheckBox ignoredFileStatus;

    /**
     * Enable outer ignore rules.
     */
    public JCheckBox outerIgnoreRules;

    /**
     * Splitter element.
     */
    private Splitter templatesSplitter;

    /**
     * File types scroll panel with table.
     */
    private JScrollPane languagesPanel;

    /**
     * {@link IgnoreLanguage} settings table.
     */
    public JBTable languagesTable;

    /**
     * Editor panel element.
     */
    private EditorPanel editorPanel;

    /**
     * Create UI components.
     */
    private void createUIComponents() {
        templatesListPanel = new TemplatesListPanel();
        editorPanel = new EditorPanel();
        editorPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        templatesSplitter = new Splitter(false, 0.3f);
        templatesSplitter.setFirstComponent(templatesListPanel);
        templatesSplitter.setSecondComponent(editorPanel);

        languagesTable = new JBTable();
        languagesTable.setModel(new LanguagesTableModel());
        languagesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        languagesTable.setColumnSelectionAllowed(false);
        languagesTable.setRowHeight(22);
        languagesTable.setPreferredScrollableViewportSize(new Dimension(-1, languagesTable.getRowHeight() * IgnoreBundle.LANGUAGES.size()));

        languagesTable.setStriped(true);
        languagesTable.setShowGrid(false);
        languagesTable.setBorder(null);
        languagesTable.setDragEnabled(false);

        languagesPanel = ScrollPaneFactory.createScrollPane(languagesTable);
    }

    @Override
    public void dispose() {
        if (!editorPanel.preview.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(editorPanel.preview);
        }
    }

    /**
     * Extension for the CRUD list panel.
     */
    public class TemplatesListPanel extends AddEditDeleteListPanel<IgnoreSettings.UserTemplate> {

        /**
         * Constructs CRUD panel with list listener for editor updating.
         */
        public TemplatesListPanel() {
            super(null, ContainerUtil.<IgnoreSettings.UserTemplate>newArrayList());
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
        @SuppressWarnings("unchecked")
        public void resetForm(List<IgnoreSettings.UserTemplate> userTemplates) {
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
            ArrayList<IgnoreSettings.UserTemplate> list = ContainerUtil.newArrayList();
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
    private class EditorPanel extends JPanel {
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
            this.preview = Utils.createPreviewEditor(previewDocument, null, false);
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

    public static class LanguagesTableModel extends AbstractTableModel {
        private final IgnoreSettings.IgnoreLanguagesSettings settings = new IgnoreSettings.IgnoreLanguagesSettings();

        private final String[] columnNames = new String[]{
                IgnoreBundle.message("settings.languagesSettings.table.name"),
                IgnoreBundle.message("settings.languagesSettings.table.newFile"),
                IgnoreBundle.message("settings.languagesSettings.table.enable")
        };

        private final Class[] columnClasses = new Class[]{
                String.class, Boolean.class, Boolean.class
        };

        /**
         * Returns the number of rows in this data table.
         *
         * @return the number of rows in the model
         */
        @Override
        public int getRowCount() {
            return settings.size();
        }

        /**
         * Returns the number of columns in this data table.
         *
         * @return the number of columns in the model
         */
        @Override
        public int getColumnCount() {
            return 3;
        }

        /**
         * Returns a default name for the column using spreadsheet conventions:
         * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
         * returns an empty string.
         *
         * @param column the column being queried
         * @return a string containing the default name of <code>column</code>
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
         *
         * @param columnIndex the column being queried
         * @return the Object.class
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        /**
         * Returns true regardless of parameter values.
         *
         * @param row    the row whose value is to be queried
         * @param column the column whose value is to be queried
         * @return true
         * @see #setValueAt
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return column != 0;
        }

        /**
         * Returns an attribute value for the cell at <code>row</code>
         * and <code>column</code>.
         *
         * @param row    the row whose value is to be queried
         * @param column the column whose value is to be queried
         * @return the value Object at the specified cell
         * @throws ArrayIndexOutOfBoundsException if an invalid row or
         *                                        column was given
         */
        @Override
        public Object getValueAt(int row, int column) {
            IgnoreLanguage language = ContainerUtil.newArrayList(settings.keySet()).get(row);
            HashMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = settings.get(language);

            switch (column) {
                case 0:
                    return language.getID();
                case 1:
                    return Boolean.valueOf(data.get(NEW_FILE).toString());
                case 2:
                    return Boolean.valueOf(data.get(ENABLE).toString());
            }
            throw new IllegalArgumentException();
        }

        /**
         * This empty implementation is provided so users don't have to implement
         * this method if their data model is not editable.
         *
         * @param value  value to assign to cell
         * @param row    row of cell
         * @param column column of cell
         */
        @Override
        public void setValueAt(Object value, int row, int column) {
            IgnoreLanguage language = ContainerUtil.newArrayList(settings.keySet()).get(row);
            HashMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = settings.get(language);

            switch (column) {
                case 1:
                    data.put(NEW_FILE, value);
                    return;
                case 2:
                    data.put(ENABLE, value);
                    return;
            }
            throw new IllegalArgumentException();
        }

        public IgnoreSettings.IgnoreLanguagesSettings getSettings() {
            return settings;
        }

        public void update(@NotNull IgnoreSettings.IgnoreLanguagesSettings settings) {
            this.settings.clear();
            this.settings.putAll(settings);
        }

        public boolean equalSettings(IgnoreSettings.IgnoreLanguagesSettings settings) {
            return this.settings.equals(settings);
        }
    }
}
