package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.actions.GitignoreRemoveEntryFix;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class GitignoreDuplicateEntryInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final MultiMap<String, GitignoreEntry> entries = MultiMap.create();

        file.acceptChildren(new GitignoreVisitor() {
            @Override
            public void visitEntry(@NotNull GitignoreEntry entry) {
                entries.putValue(entry.getText(), entry);
                super.visitEntry(entry);
            }
        });

        for (Map.Entry<String, Collection<GitignoreEntry>> stringCollectionEntry : entries.entrySet()) {
            Iterator<GitignoreEntry> iterator = stringCollectionEntry.getValue().iterator();
            iterator.next();
            while (iterator.hasNext()) {
                GitignoreEntry entry = iterator.next();
                problemsHolder.registerProblem(entry, GitignoreBundle.message("codeInspection.duplicateEntry.message"), new GitignoreRemoveEntryFix(entry));
            }
        }

        return problemsHolder.getResultsArray();
    }

    @Override
    public boolean runForWholeFile() {
        return true;
    }

}
