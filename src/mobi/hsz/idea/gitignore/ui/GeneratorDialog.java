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

import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction;
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
import java.util.List;
import java.util.Set;

/**
 * {@link GeneratorDialog} responsible for displaying list of all available templates and adding selected ones
 * to the specified file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.2
 */
public class GeneratorDialog extends DialogWrapper {
    /** {@link FilterComponent} search history key. */
    private static final String TEMPLATES_FILTER_HISTORY = "TEMPLATES_FILTER_HISTORY";

    /** Cache set to store checked templates for the current action. */
    private final Set<Resources.Template> checked = ContainerUtil.newHashSet();

    /** Current working project. */
    @NotNull private final Project project;

    /** Current working file. */
    @NotNull private final PsiFile file;

    /** Templates tree root node. */
    @NotNull private final TemplateTreeNode root;

    /** Templates tree with checkbox feature. */
    private CheckboxTree tree;

    /** Tree expander responsible for expanding and collapsing tree structure. */
    private DefaultTreeExpander treeExpander;

    /** Dynamic templates filter. */
    private FilterComponent profileFilter;

    /** Preview editor with syntax highlight. */
    private Editor preview;

    /** {@link Document} related to the {@link Editor} feature. */
    private Document previewDocument;

    /** Scroll panel for the templates tree. */
    private JScrollPane treeScrollPanel;

    /**
     * Builds a new instance of {@link GeneratorDialog}.
     *
     * @param project current working project
     * @param file current working file
     */
    public GeneratorDialog(@NotNull Project project, @NotNull PsiFile file) {
        super(project, false);
        this.project = project;
        this.file = file;
        this.root = new TemplateTreeNode();

        setTitle(IgnoreBundle.message("dialog.generator.title"));
        setOKButtonText(IgnoreBundle.message("global.generate"));
        init();
    }

    /**
     * Returns component which should be focused when the dialog appears on the screen.
     *
     * @return component to focus
     */
    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return profileFilter;
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
            String content = "";
            for (Resources.Template template : checked) {
                content += IgnoreBundle.message("file.templateSection", template.getName());
                content += "\n" + template.getContent() + "\n\n";
            }
            if (!content.isEmpty()) {
                new AppendFileCommandAction(project, file, content).execute();
            }
            super.doOKAction();
        }
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
        // general panel
        final JBPanel centerPanel = new JBPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(800, 500));

        // splitter panel - contains tree panel and preview component
        final JBSplitter splitter = new JBSplitter(false, 0.4f);
        centerPanel.add(splitter, BorderLayout.CENTER);

        final JBPanel treePanel = new JBPanel(new BorderLayout());
        previewDocument = EditorFactory.getInstance().createDocument("");
        preview = Utils.createPreviewEditor(previewDocument, project, true);

        splitter.setFirstComponent(treePanel);
        splitter.setSecondComponent(preview.getComponent());

        treeScrollPanel = createTreeScrollPanel();
        treePanel.add(treeScrollPanel, BorderLayout.CENTER);

        final JBPanel northPanel = new JBPanel(new GridBagLayout());
        northPanel.setBorder(IdeBorderFactory.createEmptyBorder(2, 0, 2, 0));
        northPanel.add(createTreeActionsToolbarPanel(treeScrollPanel).getComponent(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        northPanel.add(profileFilter, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        treePanel.add(northPanel, BorderLayout.NORTH);

        return centerPanel;
    }

    /**
     * Creates scroll panel with templates tree in it.
     *
     * @return scroll panel
     */
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

    /**
     * Creates tree toolbar panel with actions for working with templates tree.
     *
     * @param target templates tree
     * @return action toolbar
     */
    private ActionToolbar createTreeActionsToolbarPanel(JComponent target) {
        final CommonActionsManager actionManager = CommonActionsManager.getInstance();

        DefaultActionGroup actions = new DefaultActionGroup();
        actions.add(actionManager.createExpandAllAction(treeExpander, tree));
        actions.add(actionManager.createCollapseAllAction(treeExpander, tree));
        actions.add(new AnAction(IgnoreBundle.message("dialog.generator.unselectAll"), null, AllIcons.Actions.Unselectall){
            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(!checked.isEmpty());
            }

            @Override
            public void actionPerformed(AnActionEvent e) {
                checked.clear();
                filterTree(profileFilter.getTextEditor().getText());
            }
        });

        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actions, true);
        actionToolbar.setTargetComponent(target);
        return actionToolbar;
    }

    /**
     * Updates editor's content depending on the selected {@link TreePath}.
     *
     * @param path selected tree path
     */
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

    /**
     * Fills templates tree with templates fetched with {@link Resources#getGitignoreTemplates()}.
     *
     * @param filter       templates filter
     * @param forceInclude force include
     */
    private void fillTreeData(String filter, boolean forceInclude) {
        root.removeAllChildren();
        root.setChecked(false);

        for (Resources.Template.Container container : Resources.Template.Container.values()) {
            TemplateTreeNode node = new TemplateTreeNode(container);
            node.setChecked(false);
            root.add(node);
        }

        List<Resources.Template> templatesList = Resources.getGitignoreTemplates();
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

    /**
     * Creates or gets existing group node for specified element.
     *
     * @param root      tree root node
     * @param container container type to search
     * @return group node
     */
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

    /**
     * Finds for the filter's words in the given content and returns their positions.
     *
     * @param filter  templates filter
     * @param content templates content
     * @return text ranges
     */
    private List<Pair<Integer, Integer>> getFilterRanges(String filter, String content) {
        List<Pair<Integer, Integer>> pairs = ContainerUtil.newArrayList();
        content = content.toLowerCase();

        for (String word : Utils.getWords(filter)) {
            for (int index = content.indexOf(word); index >= 0; index = content.indexOf(word, index + 1)) {
                pairs.add(Pair.create(index, index + word.length()));
            }
        }

        return pairs;
    }

    /**
     * Checks if given template is accepted by passed filter.
     *
     * @param template to check
     * @param filter   templates filter
     * @return template is accepted
     */
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

    /**
     * Filters templates tree.
     *
     * @param filter text
     */
    private void filterTree(String filter) {
        if (tree != null) {
            fillTreeData(filter, true);
            reloadModel();
            TreeUtil.expandAll(tree);
            if (tree.getSelectionPath() == null) {
                TreeUtil.selectFirstNode(tree);
            }
        }
    }

    /**
     * Highlights given text ranges in {@link #preview} content.
     *
     * @param pairs text ranges
     */
    private void highlightWords(@NotNull List<Pair<Integer, Integer>> pairs) {
        final TextAttributes attr = new TextAttributes();
        attr.setBackgroundColor(UIUtil.getTreeSelectionBackground());
        attr.setForegroundColor(UIUtil.getTreeSelectionForeground());

        for (Pair<Integer, Integer> pair : pairs) {
            preview.getMarkupModel().addRangeHighlighter(pair.first, pair.second, 0, attr, HighlighterTargetArea.EXACT_RANGE);
        }
    }

    /**
     * Reloads tree model.
     */
    private void reloadModel() {
        ((DefaultTreeModel) tree.getModel()).reload();
    }

    /**
     * Custom templates {@link FilterComponent}.
     */
    private class TemplatesFilterComponent extends FilterComponent {
        /** Builds a new instance of {@link TemplatesFilterComponent}. */
        public TemplatesFilterComponent() {
            super(TEMPLATES_FILTER_HISTORY, 10);
        }

        /** Filters tree using current filter's value. */
        @Override
        public void filter() {
            filterTree(getFilter());
        }
    }
}
