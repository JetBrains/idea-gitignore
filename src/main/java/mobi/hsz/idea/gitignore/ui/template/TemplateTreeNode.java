// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template;

import com.intellij.ui.CheckedTreeNode;
import mobi.hsz.idea.gitignore.util.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link TemplateTreeNode} is an implementation of checkbox tree node.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6
 */
public class TemplateTreeNode extends CheckedTreeNode {
    /** Current {@link Resources.Template} element. */
    @Nullable
    private final Resources.Template template;

    /** Current {@link Resources.Template.Container}. */
    @Nullable
    private final Resources.Template.Container container;

    /** Creates a new instance of {@link TemplateTreeNode}. */
    public TemplateTreeNode() {
        super(null);
        this.template = null;
        this.container = null;
    }

    /**
     * Creates a new instance of {@link TemplateTreeNode}.
     *
     * @param container current templates container
     */
    public TemplateTreeNode(@Nullable Resources.Template.Container container) {
        super(container);
        this.template = null;
        this.container = container;
    }

    /**
     * Creates a new instance of {@link TemplateTreeNode}.
     *
     * @param template current template
     */
    public TemplateTreeNode(@NotNull Resources.Template template) {
        super(template);
        this.template = template;
        this.container = template.getContainer();
    }

    /**
     * Returns current template.
     *
     * @return template or null if container node
     */
    @Nullable
    public Resources.Template getTemplate() {
        return template;
    }

    /**
     * Returns current container.
     *
     * @return template container
     */
    @Nullable
    public Resources.Template.Container getContainer() {
        return container;
    }
}
