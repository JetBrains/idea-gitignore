/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.projectView;

import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.intellij.ui.SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES;

/**
 * Extension for the {@link TreeStructureProvider} that provides the ability to hide ignored files
 * or directories in the project tree view.
 *
 * @author Maximiliano Najle <maximilianonajle@gmail.com>
 * @since 1.7
 */
public class HideIgnoredFilesTreeStructureProvider implements TreeStructureProvider {
    /** {@link IgnoreSettings} instance. */
    @NotNull
    private final IgnoreSettings ignoreSettings;

    /** {@link IgnoreManager} instance. */
    @NotNull
    private final IgnoreManager ignoreManager;

    /** {@link FileStatusManager} instance. */
    @NotNull
    private final FileStatusManager statusManager;

    /** Builds a new instance of {@link HideIgnoredFilesTreeStructureProvider}. */
    public HideIgnoredFilesTreeStructureProvider(@NotNull Project project) {
        this.ignoreSettings = IgnoreSettings.getInstance();
        this.ignoreManager = IgnoreManager.getInstance(project);
        this.statusManager = FileStatusManager.getInstance(project);
    }

    /**
     * If {@link IgnoreSettings#hideIgnoredFiles} is set to <code>true</code>, checks if specific
     * nodes are ignored and filters them out.
     *
     * @param parent   the parent node
     * @param children the list of child nodes according to the default project structure
     * @param settings the current project view settings
     * @return the modified collection of child nodes
     */
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {

        if (!ignoreSettings.isHideIgnoredFiles()) {
            return children;
        }

        final List<AbstractTreeNode> result = ContainerUtil.filter(children, new Condition<AbstractTreeNode>() {
            @Override
            public boolean value(AbstractTreeNode node) {
                if (node instanceof ProjectViewNode) {
                    final VirtualFile file = ((ProjectViewNode) node).getVirtualFile();
                    if (file != null) {
                        return !ignoreManager.isFileIgnored(file) || ignoreManager.isFileIgnoredAndTracked(file);
                    }
                }
                return true;
            }
        });

        int diff = children.size() - result.size();
        if (diff > 0) {
            Utils.addColoredText(
                    parent.getPresentation(),
                    IgnoreBundle.message("projectView.containsHidden", diff),
                    GRAYED_SMALL_ATTRIBUTES
            );
//            statusManager.fileStatusesChanged();
//            statusManager.fileStatusChanged(parent);
        }

        return result;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }
}
