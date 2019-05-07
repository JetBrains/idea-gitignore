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

package mobi.hsz.idea.gitignore.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.AddEditDeleteListPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.ui.table.JBTable;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBUI;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Constants;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static mobi.hsz.idea.gitignore.settings.IgnoreSettings.IgnoreLanguagesSettings.KEY.ENABLE;
import static mobi.hsz.idea.gitignore.settings.IgnoreSettings.IgnoreLanguagesSettings.KEY.NEW_FILE;

/**
 * UI form for {@link IgnoreSettings} edition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6.1
 */
public class IgnoreSettingsPanel implements Disposable {
    /** The parent panel for the form. */
    public JPanel panel;

    /** Form element for IgnoreSettings#missingGitignore. */
    private JCheckBox missingGitignore;

    /** Templates list panel. */
    private TemplatesListPanel templatesListPanel;

    /** Enable ignored file status coloring. */
    private JCheckBox ignoredFileStatus;

    /** Enable outer ignore rules. */
    private JCheckBox outerIgnoreRules;

    /** Defines if new content should be inserted at the cursor's position or at the document end. */
    private JCheckBox insertAtCursor;

    /** Suggest to add unversioned files to the .gitignore file. */
    private JCheckBox addUnversionedFiles;

    /** Splitter element. */
    private Splitter templatesSplitter;

    /** File types scroll panel with table. */
    private JScrollPane languagesPanel;

    /** {@link IgnoreLanguage} settings table. */
    private JBTable languagesTable;

    /** Enable unignore files group. */
    public JCheckBox unignoreFiles;

    /** Panel with information about donations. */
    private JPanel donatePanel;

    /** Inform about editing ignored file. */
    private JCheckBox notifyIgnoredEditing;

    /** Editor panel element. */
    private EditorPanel editorPanel;

    /** Create UI components. */
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
        languagesTable.setPreferredScrollableViewportSize(new Dimension(-1,
                languagesTable.getRowHeight() * IgnoreBundle.LANGUAGES.size() / 2));

        languagesTable.setStriped(true);
        languagesTable.setShowGrid(false);
        languagesTable.setBorder(JBUI.Borders.empty());
        languagesTable.setDragEnabled(false);

        languagesPanel = ScrollPaneFactory.createScrollPane(languagesTable);

        donatePanel = new JBPanel(new BorderLayout());
        donatePanel.setBorder(JBUI.Borders.empty(10, 0));
        donatePanel.add(new JBLabel(IgnoreBundle.message("settings.general.donate")), BorderLayout.WEST);
        donatePanel.add(createLink(
                "Donate with PayPal",
                "https://www.paypal.me/hsz"
        ), BorderLayout.CENTER);
    }

    /**
     * Creates {@link ActionLink} component with URL open action.
     *
     * @param title title of link
     * @param url   url to open
     * @return {@link ActionLink} component
     */
    private ActionLink createLink(@NotNull String title, @NotNull final String url) {
        final ActionLink action = new ActionLink(title, new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                BrowserUtil.browse(url);
            }
        });
        action.setBorder(JBUI.Borders.empty(0, 5));
        return action;
    }

    /** Disposes current preview {@link #editorPanel}. */
    @Override
    public void dispose() {
        if (!editorPanel.preview.isDisposed()) {
            EditorFactory.getInstance().releaseEditor(editorPanel.preview);
        }
    }

    /**
     * Returns value of @{link {@link #missingGitignore}} field.
     *
     * @return {@link #missingGitignore} is selected
     */
    public boolean isMissingGitignore() {
        return missingGitignore.isSelected();
    }

    /**
     * Sets value of {@link #missingGitignore} field.
     *
     * @param selected value for {@link #missingGitignore}
     */
    public void setMissingGitignore(boolean selected) {
        this.missingGitignore.setSelected(selected);
    }

    /**
     * Returns value of @{link {@link #ignoredFileStatus}} field.
     *
     * @return {@link #ignoredFileStatus} is selected
     */
    public boolean isIgnoredFileStatus() {
        return ignoredFileStatus.isSelected();
    }

    /**
     * Sets value of {@link #ignoredFileStatus} field.
     *
     * @param selected value for {@link #ignoredFileStatus}
     */
    public void setIgnoredFileStatus(boolean selected) {
        this.ignoredFileStatus.setSelected(selected);
    }

    /**
     * Returns {@link IgnoreSettings.UserTemplate} list of {@link #templatesListPanel}.
     *
     * @return {@link IgnoreSettings.UserTemplate} list
     */
    @NotNull
    public List<IgnoreSettings.UserTemplate> getUserTemplates() {
        return this.templatesListPanel.getList();
    }

    /**
     * Sets new {@link IgnoreSettings.UserTemplate} list to {@link #templatesListPanel}.
     *
     * @param userTemplates {@link IgnoreSettings.UserTemplate} list
     */
    public void setUserTemplates(@NotNull List<IgnoreSettings.UserTemplate> userTemplates) {
        this.templatesListPanel.resetForm(userTemplates);
    }

    /**
     * Returns value of @{link {@link #outerIgnoreRules}} field.
     *
     * @return {@link #outerIgnoreRules} is selected
     */
    public boolean isOuterIgnoreRules() {
        return outerIgnoreRules.isSelected();
    }

    /**
     * Sets value of {@link #outerIgnoreRules} field.
     *
     * @param selected value for {@link #outerIgnoreRules}
     */
    public void setOuterIgnoreRules(boolean selected) {
        this.outerIgnoreRules.setSelected(selected);
    }

    /**
     * Returns value of @{link {@link #insertAtCursor}} field.
     *
     * @return {@link #insertAtCursor} is selected
     */
    public boolean isInsertAtCursor() {
        return insertAtCursor.isSelected();
    }

    /**
     * Sets value of {@link #insertAtCursor} field.
     *
     * @param selected value for {@link #insertAtCursor}
     */
    public void setInsertAtCursor(boolean selected) {
        this.insertAtCursor.setSelected(selected);
    }

    /**
     * Returns value of @{link {@link #addUnversionedFiles}} field.
     *
     * @return {@link #addUnversionedFiles} is selected
     */
    public boolean isAddUnversionedFiles() {
        return addUnversionedFiles.isSelected();
    }

    /**
     * Sets value of {@link #addUnversionedFiles} field.
     *
     * @param selected value for {@link #addUnversionedFiles}
     */
    public void setAddUnversionedFiles(boolean selected) {
        this.addUnversionedFiles.setSelected(selected);
    }

    /**
     * Returns value of @{link {@link #unignoreFiles}} field.
     *
     * @return {@link #unignoreFiles} is selected
     */
    public boolean isUnignoreActions() {
        return unignoreFiles.isSelected();
    }

    /**
     * Sets value of {@link #unignoreFiles} field.
     *
     * @param selected value for {@link #unignoreFiles}
     */
    public void setUnignoreActions(boolean selected) {
        this.unignoreFiles.setSelected(selected);
    }

    /**
     * Returns value of @{link {@link #notifyIgnoredEditing}} field.
     *
     * @return {@link #notifyIgnoredEditing} is selected
     */
    public boolean isNotifyIgnoredEditing() {
        return notifyIgnoredEditing.isSelected();
    }

    /**
     * Sets value of {@link #notifyIgnoredEditing} field.
     *
     * @param selected value for {@link #notifyIgnoredEditing}
     */
    public void setNotifyIgnoredEditing(boolean selected) {
        this.notifyIgnoredEditing.setSelected(selected);
    }

    /**
     * Returns model of {@link #languagesTable}.
     *
     * @return {@link #languagesTable} model
     */
    public LanguagesTableModel getLanguagesSettings() {
        return (LanguagesTableModel) this.languagesTable.getModel();
    }

    /** Extension for the CRUD list panel. */
    public class TemplatesListPanel extends AddEditDeleteListPanel<IgnoreSettings.UserTemplate> {
        /** Import/export file's extension. */
        private static final String FILE_EXTENSION = "xml";

        /** Constructs CRUD panel with list listener for editor updating. */
        public TemplatesListPanel() {
            super(null, ContainerUtil.newArrayList());
            myList.addListSelectionListener(e -> {
                boolean enabled = myListModel.size() > 0;
                editorPanel.setEnabled(enabled);

                if (enabled) {
                    IgnoreSettings.UserTemplate template = getCurrentItem();
                    editorPanel.setContent(template != null ? template.getContent() : "");
                }
            });
        }

        /**
         * Customizes Import dialog.
         *
         * @param decorator toolbar
         */
        @Override
        protected void customizeDecorator(ToolbarDecorator decorator) {
            super.customizeDecorator(decorator);

            final DefaultActionGroup group = new DefaultActionGroup();
            group.addSeparator();

            group.add(new AnAction(
                    IgnoreBundle.message("action.importTemplates"),
                    IgnoreBundle.message("action.importTemplates.description"),
                    AllIcons.Actions.Install
            ) {
                @SuppressWarnings("unchecked")
                @Override
                public void actionPerformed(@NotNull final AnActionEvent event) {
                    final FileChooserDescriptor descriptor =
                            new FileChooserDescriptor(true, false, true, false, true, false) {
                                @Override
                                public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                                    return super.isFileVisible(file, showHiddenFiles) &&
                                            (file.isDirectory() || FILE_EXTENSION.equals(file.getExtension()) ||
                                                    file.getFileType() == FileTypes.ARCHIVE);
                                }

                                @Override
                                public boolean isFileSelectable(VirtualFile file) {
                                    return file.getFileType() == StdFileTypes.XML;
                                }
                            };
                    descriptor.setDescription(IgnoreBundle.message("action.importTemplates.wrapper.description"));
                    descriptor.setTitle(IgnoreBundle.message("action.importTemplates.wrapper"));
                    descriptor.putUserData(
                            LangDataKeys.MODULE_CONTEXT,
                            LangDataKeys.MODULE.getData(event.getDataContext())
                    );

                    final VirtualFile file = FileChooser.chooseFile(descriptor, templatesListPanel, null, null);
                    if (file != null) {
                        try {
                            final Element element = JDOMUtil.load(file.getInputStream());
                            List<IgnoreSettings.UserTemplate> templates = IgnoreSettings.loadTemplates(element);
                            for (IgnoreSettings.UserTemplate template : templates) {
                                myListModel.addElement(template);
                            }
                            Messages.showInfoMessage(templatesListPanel,
                                    IgnoreBundle.message("action.importTemplates.success", templates.size()),
                                    IgnoreBundle.message("action.exportTemplates.success.title"));
                            return;
                        } catch (IOException | JDOMException e) {
                            e.printStackTrace();
                        }
                    }

                    Messages.showErrorDialog(templatesListPanel, IgnoreBundle.message("action.importTemplates.error"));
                }
            });

            group.add(new AnAction(
                    IgnoreBundle.message("action.exportTemplates"),
                    IgnoreBundle.message("action.exportTemplates.description"),
                    AllIcons.ToolbarDecorator.Export
            ) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent event) {
                    final VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(
                            new FileSaverDescriptor(
                                    IgnoreBundle.message("action.exportTemplates.wrapper"),
                                    "",
                                    FILE_EXTENSION
                            ),
                            templatesListPanel
                    ).save(null, null);

                    if (wrapper != null) {
                        final List<IgnoreSettings.UserTemplate> items = getCurrentItems();
                        final org.jdom.Document document = new org.jdom.Document(
                                IgnoreSettings.createTemplatesElement(items)
                        );
                        try {
                            JDOMUtil.writeDocument(document, wrapper.getFile(), Constants.NEWLINE);
                            Messages.showInfoMessage(templatesListPanel,
                                    IgnoreBundle.message("action.exportTemplates.success", items.size()),
                                    IgnoreBundle.message("action.exportTemplates.success.title"));
                        } catch (IOException e) {
                            Messages.showErrorDialog(
                                    templatesListPanel,
                                    IgnoreBundle.message("action.exportTemplates.error")
                            );
                        }
                    }
                }

                @Override
                public void update(@NotNull AnActionEvent e) {
                    e.getPresentation().setEnabled(getCurrentItems().size() > 0);
                }
            });
            decorator.setActionGroup(group);
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
         * Shows edit dialog and validates user's input name.
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
        public void resetForm(@NotNull List<IgnoreSettings.UserTemplate> userTemplates) {
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
        protected IgnoreSettings.UserTemplate editSelectedItem(@NotNull IgnoreSettings.UserTemplate item) {
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
                list.add(myListModel.getElementAt(i));
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
            return myListModel.get(index);
        }

        /**
         * Returns selected {@link IgnoreSettings.UserTemplate} elements.
         *
         * @return {@link IgnoreSettings.UserTemplate} list
         */
        public List<IgnoreSettings.UserTemplate> getCurrentItems() {
            List<IgnoreSettings.UserTemplate> list = ContainerUtil.newArrayList();
            int[] ids = myList.getSelectedIndices();
            for (int i = 0; i < ids.length; i++) {
                list.add(getList().get(i));
            }
            return list;
        }
    }

    /** Editor panel class that displays document editor or label if no template is selected. */
    private class EditorPanel extends JPanel {
        /** Preview editor. */
        private final Editor preview;

        /** `No templates is selected` label. */
        private final JBLabel label;

        /** Preview document. */
        private final Document previewDocument;

        /** Constructor that creates document editor, empty content label. */
        public EditorPanel() {
            super(new BorderLayout());
            this.previewDocument = EditorFactory.getInstance().createDocument("");
            this.label = new JBLabel(IgnoreBundle.message("settings.userTemplates.noTemplateSelected"), JBLabel.CENTER);
            this.preview = Utils.createPreviewEditor(previewDocument, null, false);
            this.preview.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void beforeDocumentChange(@NotNull DocumentEvent event) {
                }

                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
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
            ApplicationManager.getApplication().runWriteAction(
                    () -> CommandProcessor.getInstance().runUndoTransparentAction(
                            () -> previewDocument.replaceString(0, previewDocument.getTextLength(), content)
                    )
            );
        }
    }

    /** Languages table helper class. */
    public static class LanguagesTableModel extends AbstractTableModel {
        /** Languages settings instance. */
        private final IgnoreSettings.IgnoreLanguagesSettings settings = new IgnoreSettings.IgnoreLanguagesSettings();

        /** Table's columns names. */
        private final String[] columnNames = new String[]{
                IgnoreBundle.message("settings.languagesSettings.table.name"),
                IgnoreBundle.message("settings.languagesSettings.table.newFile"),
                IgnoreBundle.message("settings.languagesSettings.table.enable")
        };

        /** Table's columns classes. */
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
            return columnNames.length;
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
         *
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
         *
         * @throws ArrayIndexOutOfBoundsException if an invalid row or column was given
         */
        @Override
        public Object getValueAt(int row, int column) {
            final IgnoreLanguage language = ContainerUtil.newArrayList(settings.keySet()).get(row);
            if (language == null) {
                return null;
            }
            final TreeMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = settings.get(language);

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
            TreeMap<IgnoreSettings.IgnoreLanguagesSettings.KEY, Object> data = settings.get(language);

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

        /**
         * Returns current settings.
         *
         * @return settings
         */
        public IgnoreSettings.IgnoreLanguagesSettings getSettings() {
            return settings;
        }

        /**
         * Update settings model.
         *
         * @param settings to update
         */
        public void update(@NotNull IgnoreSettings.IgnoreLanguagesSettings settings) {
            this.settings.clear();
            this.settings.putAll(settings);
        }

        /**
         * Checks if current settings are equal to the given one.
         *
         * @param settings to check
         * @return equals
         */
        public boolean equalSettings(IgnoreSettings.IgnoreLanguagesSettings settings) {
            return this.settings.equals(settings);
        }
    }
}
