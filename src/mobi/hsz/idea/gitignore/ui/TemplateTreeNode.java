/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
