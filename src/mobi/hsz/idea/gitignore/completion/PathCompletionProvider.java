package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import org.jetbrains.annotations.NotNull;

public class PathCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

        LeafPsiElement element = (LeafPsiElement) parameters.getOriginalPosition();
        PsiDirectory root = parameters.getOriginalFile().getParent();

        if (root != null) {
            if (element != null) {
                IElementType type = element.getElementType();
                String entry = element.getText();
                if (type.equals(GitignoreTypes.ENTRY_DIRECTORY)) {
                    root = findSubdirectory(root, entry);
                } else {
                    int index = entry.lastIndexOf("/");
                    if (index != -1) {
                        root = findSubdirectory(root, entry.substring(0, index));
                    }
                }
            }

            if (root != null) {
                VirtualFile[] children = root.getVirtualFile().getChildren();
                String rootPath = getRootPath(root, parameters.getOriginalFile().getContainingDirectory());
                for (VirtualFile child : children) {
                    result.addElement(new PathLookupElement(rootPath + child.getName(), child.getFileType(), child.isDirectory()));
                }
            }
        }
    }

    /**
     * Returns full path relative to the container directory of currently opened file
     *
     * @param root End-path directory
     * @param absoluteRoot Start-path directory
     * @return Full path between start- and end-path directories
     */
    private String getRootPath(PsiDirectory root, PsiDirectory absoluteRoot) {
        if (root == null || root.equals(absoluteRoot)) {
            return "";
        }
        return getRootPath(root.getParent(), absoluteRoot) + root.getName() + "/";
    }

    /**
     * Returns the most nested directory by given path
     *
     * @param root Current directory
     * @param path Path
     * @return The most nested directory
     */
    private PsiDirectory findSubdirectory(PsiDirectory root, String path) {
        String[] parts = path.split("/");
        if (parts.length > 0) {
            root = root.findSubdirectory(parts[0]);
            path = path.substring(parts[0].length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }
        if (root != null && !path.equals("")) {
            return findSubdirectory(root, path);
        }
        return root;
    }
}
