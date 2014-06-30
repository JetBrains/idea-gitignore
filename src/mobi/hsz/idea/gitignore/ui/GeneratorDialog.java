package mobi.hsz.idea.gitignore.ui;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import mobi.hsz.idea.gitignore.util.Resources;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.Arrays;

public class GeneratorDialog extends JDialog {
    private final PsiFile file;
    private final Project project;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField search;
    private JList list;
    private FilterableListModel<Resources.Template> listModel;
    private JTextArea preview;

    public GeneratorDialog(Project project, PsiFile file) {
        this.project = project;
        this.file = file;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String content = "";

        for (Object object : getListItems()) {
            Resources.Template template = (Resources.Template) object;
            content += "\n### " + template.getName() + " template\n" + template.getContent();
        }

        if (!content.equals("")) {
            final String finalContent = content;
            new WriteCommandAction<PsiFile>(project) {
                @Override
                protected void run(@NotNull Result<PsiFile> result) throws Throwable {
                    Document document = PsiDocumentManager.getInstance(project).getDocument(file);
                    if (document != null) {
                        document.insertString(document.getTextLength(), finalContent);
                        PsiDocumentManager.getInstance(project).commitDocument(document);
                    }
                }
            }.execute();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        setTitle("Gitignore file generator");
        listModel = new FilterableListModel<Resources.Template>();
        listModel.setElements(Resources.getGitignoreTemplates());
        list = new JBList(listModel);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Resources.Template template = (Resources.Template) list.getSelectedValue();
                String content = template != null ? template.getContent() : "";
                preview.setText(content);
            }
        });

        search = new JBTextField();
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearch();
            }
        });
        preview = new JTextArea();
    }

    public void onSearch() {
        list.clearSelection();
        listModel.filter(search.getText());
    }

    @SuppressWarnings("deprecation" )
    private Iterable<?> getListItems() {
        return Utils.JAVA_VERSION > 1.6 ? list.getSelectedValuesList() : Arrays.asList(list.getSelectedValues());
    }
}
