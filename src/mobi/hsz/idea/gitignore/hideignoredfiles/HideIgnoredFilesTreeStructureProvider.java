package mobi.hsz.idea.gitignore.hideignoredfiles;

import com.intellij.ide.projectView.*;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.*;

import java.util.*;

public class HideIgnoredFilesTreeStructureProvider implements TreeStructureProvider {

    private final Project project = ProjectManager.getInstance().getOpenProjects()[0];
    private final IgnoreManager ignoreManager = IgnoreManager.getInstance(project);

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {

        if (! IgnoreSettings.getInstance().shouldHideIgnoredFilesOnProjectView()) {
            return children;
        }

        ArrayList<AbstractTreeNode> nodes = new ArrayList<AbstractTreeNode>();
        for (AbstractTreeNode child : children) {
            if (!isAnIgnoredFile(child)) {
                nodes.add(child);
            }
        }
        return nodes;
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }

    private boolean isAnIgnoredFile(AbstractTreeNode node) {
        try {
            VirtualFile file = ((PsiFileNode) node).getVirtualFile();
            return ignoreManager.isFileIgnored(file);
        }
        catch (Exception e) {
            try {
                VirtualFile file = ((PsiDirectoryNode) node).getVirtualFile();
                return ignoreManager.isFileIgnored(file);
            }
            catch (Exception ex) {
                return false;
            }
        }
    }

}