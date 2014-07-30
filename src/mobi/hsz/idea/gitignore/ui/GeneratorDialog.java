package mobi.hsz.idea.gitignore.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.speedSearch.ListWithFilter;
import com.intellij.util.Function;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import mobi.hsz.idea.gitignore.util.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class GeneratorDialog extends DialogWrapper {
    @NotNull private final Project myProject;
    @NotNull private final PsiFile myFile;
    @NotNull private final Editor preview;
    @NotNull private final JBList list;
    @NotNull private final Document previewDocument;

    public GeneratorDialog(@NotNull Project project, @NotNull PsiFile file) {
        super(project, false);
        myProject = project;
        myFile = file;

        List<Resources.Template> templatesList = Resources.getGitignoreTemplates();
        Collections.sort(templatesList);
        list = new JBList(templatesList);
        previewDocument = EditorFactory.getInstance().createDocument("");
        preview = createPreviewEditor(project, previewDocument);

        setTitle(GitignoreBundle.message("dialog.generator.title"));
        setOKButtonText(GitignoreBundle.message("global.generate"));
        init();
    }

    @NotNull
    private static Editor createPreviewEditor(@NotNull Project project, @NotNull Document document) {
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, GitignoreFileType.INSTANCE, true);
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
        editor.setCaretEnabled(false);

        EditorColorsScheme colorsScheme = editor.getColorsScheme();
        colorsScheme.setColor(EditorColors.CARET_ROW_COLOR, null);
        return editor;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return list;
    }

    @Override
    protected void dispose() {
        EditorFactory.getInstance().releaseEditor(preview);
        super.dispose();
    }

    @Override
    protected void doOKAction() {
        if (isOKActionEnabled()) {
            Object selectedValue = list.getSelectedValue();
            if (selectedValue != null) {
                Resources.Template template = (Resources.Template) selectedValue;
                String content = "### " + template.getName() + " template\n" + template.getContent();
                new AppendFileCommandAction(myProject, myFile, content).execute();
            }
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final Resources.Template template = (Resources.Template) list.getSelectedValue();
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                            @Override
                            public void run() {
                                String content = template != null ? StringUtil.replaceChar(template.getContent(), '\r', '\0') : "";
                                previewDocument.replaceString(0, previewDocument.getTextLength(), content);
                            }
                        });
                    }
                });
            }
        });

        JComponent listComponent = ListWithFilter.wrap(list, ScrollPaneFactory.createScrollPane(list), new Function<Resources.Template, String>() {
            @Override
            public String fun(Resources.Template template) {
                return template.getName();
            }
        });

        JBSplitter splitter = new JBSplitter(0.3f);
        splitter.setFirstComponent(listComponent);
        splitter.setSecondComponent(preview.getComponent());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(splitter, BorderLayout.CENTER);
        centerPanel.setPreferredSize(new Dimension(700, 300));
        return centerPanel;
    }
}
