package mobi.hsz.idea.gitignore.ui;

import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.containers.hash.HashSet;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import mobi.hsz.idea.gitignore.util.Resources;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneratorDialog extends DialogWrapper {
    private static final String TEMPLATES_FILTER_HISTORY = "TEMPLATES_FILTER_HISTORY";
    private final Set<Resources.Template> checked = new HashSet<Resources.Template>();

    @NotNull private final Project project;
    @NotNull private final PsiFile file;
    @NotNull private final TemplateTreeNode root;

    private CheckboxTree tree;
    private DefaultTreeExpander treeExpander;
    private FilterComponent profileFilter;
    private Editor preview;
    private Document previewDocument;
    private JScrollPane treeScrollPanel;

    public GeneratorDialog(@NotNull Project project, @NotNull PsiFile file) {
        super(project, false);
        this.project = project;
        this.file = file;
        this.root = new TemplateTreeNode();

        setTitle(GitignoreBundle.message("dialog.generator.title"));
        setOKButtonText(GitignoreBundle.message("global.generate"));
        init();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return treeScrollPanel;
    }

    @Override
    protected void dispose() {
        EditorFactory.getInstance().releaseEditor(preview);
        super.dispose();
    }

    @Override
    protected void doOKAction() {
        if (isOKActionEnabled()) {
            String content = "";
            for (Resources.Template template : checked) {
                content += "### " + template.getName() + " template\n" + template.getContent() + "\n\n";
            }
            if (!content.equals("")) {
                new AppendFileCommandAction(project, file, content).execute();
            }
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // general panel
        final JBPanel centerPanel = new JBPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(800, 400));

        // splitter panel - contains tree panel and preview component
        final JBSplitter splitter = new JBSplitter(0.3f);
        centerPanel.add(splitter, BorderLayout.CENTER);

        final JBPanel treePanel = new JBPanel(new BorderLayout());
        previewDocument = EditorFactory.getInstance().createDocument("");
        preview = createPreviewEditor(project, previewDocument);

        splitter.setFirstComponent(treePanel);
        splitter.setSecondComponent(preview.getComponent());

        treeScrollPanel = createTreeScrollPanel();
        treePanel.add(treeScrollPanel, BorderLayout.CENTER);

        final JBPanel northPanel = new JBPanel(new GridBagLayout());
        northPanel.setBorder(IdeBorderFactory.createEmptyBorder(2, 0, 2, 0));
        northPanel.add(createTreeToolbarPanel(treeScrollPanel).getComponent(), new GridBagConstraints(0, 0, 1, 1, 0.5, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        northPanel.add(profileFilter, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        treePanel.add(northPanel, BorderLayout.NORTH);

        return centerPanel;
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

    private JScrollPane createTreeScrollPanel() {
        fillTreeData(null, true);

        final TemplateTreeRenderer renderer = new TemplateTreeRenderer() {
            protected String getFilter() {
                return profileFilter != null ? profileFilter.getFilter() : null;
            }
        };

        tree = new CheckboxTree(renderer, root) {
            public Dimension getPreferredScrollableViewportSize() {
                Dimension size = super.getPreferredScrollableViewportSize();
                size = new Dimension(size.width + 10, size.height);
                return size;
            }

            @Override
            protected void onNodeStateChanged(CheckedTreeNode node) {
                super.onNodeStateChanged(node);
                Resources.Template template = ((TemplateTreeNode) node).getTemplate();
                if (node.isChecked()) {
                    checked.add(template);
                } else {
                    checked.remove(template);
                }
            }
        };

        tree.setCellRenderer(renderer);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        UIUtil.setLineStyleAngled(tree);
        TreeUtil.installActions(tree);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (tree.getSelectionPaths() != null && tree.getSelectionPaths().length == 1) {
                    updateDescriptionPanel(tree.getSelectionPaths()[0]);
                }
            }
        });


        final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(tree);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TreeUtil.expandAll(tree);

        treeExpander = new DefaultTreeExpander(tree);
        profileFilter = new TemplatesFilterComponent();

        return scrollPane;
    }

    private ActionToolbar createTreeToolbarPanel(JComponent target) {
        final CommonActionsManager actionManager = CommonActionsManager.getInstance();

        DefaultActionGroup actions = new DefaultActionGroup();
        actions.add(actionManager.createExpandAllAction(treeExpander, tree));
        actions.add(actionManager.createCollapseAllAction(treeExpander, tree));
        actions.addSeparator();

        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);
        actionToolbar.setTargetComponent(target);
        return actionToolbar;
    }

    private void updateDescriptionPanel(TreePath path) {
        final TemplateTreeNode node = (TemplateTreeNode) path.getLastPathComponent();
        final Resources.Template template = node.getTemplate();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                    @Override
                    public void run() {
                        String content = template != null ? StringUtil.replaceChar(template.getContent(), '\r', '\0') : "";
                        previewDocument.replaceString(0, previewDocument.getTextLength(), content);

                        List<Pair<Integer, Integer>> pairs = getFilterRanges(profileFilter.getTextEditor().getText(), content);
                        highlightWords(pairs);
                    }
                });
            }
        });
    }

    private void fillTreeData(String filter, boolean forceInclude) {
        root.removeAllChildren();
        root.setChecked(false);

        for (Resources.Template.Container container : Resources.Template.Container.values()) {
            TemplateTreeNode node = new TemplateTreeNode(container);
            node.setChecked(false);
            root.add(node);
        }

        java.util.List<Resources.Template> templatesList = Resources.getGitignoreTemplates();
        for (Resources.Template template : templatesList) {
            if (filter != null && filter.length() > 0 && !isTemplateAccepted(template, filter)) {
                continue;
            }

            final TemplateTreeNode node = new TemplateTreeNode(template);
            node.setChecked(checked.contains(template));
            getGroupNode(root, template.getContainer()).add(node);
        }

        if (filter != null && forceInclude && root.getChildCount() == 0) {
            fillTreeData(filter, false);
        }

        TreeUtil.sort(root, new TemplateTreeComparator());
    }

    private static TemplateTreeNode getGroupNode(TemplateTreeNode root, Resources.Template.Container container) {
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            TemplateTreeNode child = (TemplateTreeNode) root.getChildAt(i);
            if (container.equals(child.getContainer())) {
                return child;
            }
        }

        TemplateTreeNode child = new TemplateTreeNode(container);
        root.add(child);
        return child;
    }

    private List<Pair<Integer, Integer>> getFilterRanges(String filter, String content) {
        java.util.List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>();
        content = content.toLowerCase();

        for (String word : Utils.getWords(filter)) {
            for (int index = content.indexOf(word); index >= 0; index = content.indexOf(word, index + 1)) {
                pairs.add(new Pair<Integer, Integer>(index, index + word.length()));
            }
        }

        return pairs;
    }

    private boolean isTemplateAccepted(Resources.Template template, String filter) {
        filter = filter.toLowerCase();

        if (StringUtil.containsIgnoreCase(template.getName(), filter)) {
            return true;
        }

        boolean nameAccepted = true;
        for (String word : Utils.getWords(filter)) {
            if (!StringUtil.containsIgnoreCase(template.getName(), word)) {
                nameAccepted = false;
            }
        }

        List<Pair<Integer, Integer>> ranges = getFilterRanges(filter, template.getContent());
        return nameAccepted || ranges.size() > 0;
    }

    public void filterTree(String filter) {
        if (tree != null) {
            fillTreeData(filter, true);
            reloadModel();
            TreeUtil.expandAll(tree);
            if (tree.getSelectionPath() == null) {
                TreeUtil.selectFirstNode(tree);
            }
        }
    }

    private void highlightWords(@NotNull List<Pair<Integer, Integer>> pairs) {
        final TextAttributes attr = new TextAttributes();
        attr.setBackgroundColor(UIUtil.getTreeSelectionBackground());
        attr.setForegroundColor(UIUtil.getTreeSelectionForeground());

        for (Pair<Integer, Integer> pair : pairs) {
            preview.getMarkupModel().addRangeHighlighter(pair.first, pair.second, 0, attr, HighlighterTargetArea.EXACT_RANGE);
        }
    }

    private void reloadModel() {
        ((DefaultTreeModel) tree.getModel()).reload();
    }

    private class TemplatesFilterComponent extends FilterComponent {
        public TemplatesFilterComponent() {
            super(TEMPLATES_FILTER_HISTORY, 10);
        }

        @Override
        public void filter() {
            filterTree(getFilter());
        }
    }
}
