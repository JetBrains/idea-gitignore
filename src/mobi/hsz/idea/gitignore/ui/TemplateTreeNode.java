package mobi.hsz.idea.gitignore.ui;

import com.intellij.ui.CheckedTreeNode;
import mobi.hsz.idea.gitignore.util.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateTreeNode extends CheckedTreeNode {

    @Nullable
    private final Resources.Template template;

    @Nullable
    private final Resources.Template.Container container;

    public TemplateTreeNode() {
        super();
        this.template = null;
        this.container = null;
    }

    public TemplateTreeNode(@NotNull Resources.Template.Container container) {
        super(container);
        this.template = null;
        this.container = container;
    }

    public TemplateTreeNode(@NotNull Resources.Template template) {
        super(template);
        this.template = template;
        this.container = template.getContainer();
    }

    @Nullable
    public Resources.Template getTemplate() {
        return template;
    }

    @Nullable
    public Resources.Template.Container getContainer() {
        return container;
    }
}
