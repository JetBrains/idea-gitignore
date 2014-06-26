package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PathCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

        PsiDirectory root = parameters.getOriginalFile().getParent();
        if (root != null) {
            VirtualFile[] children = root.getVirtualFile().getChildren();
            for (VirtualFile child : children) {
                result.addElement(new PathLookupElement(child.getName(), child.isDirectory()));
            }
        }
    }
}
