package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Processor;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreFile;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitignoreUnusedEntryInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<GitignoreEntry> entries = new ArrayList<GitignoreEntry>();

        if (file instanceof GitignoreFile) {
            new Processor<PsiFile>() {
                @Override
                public boolean process(PsiFile file) {
                    Map<GitignoreEntry, List<String>> map = new HashMap<GitignoreEntry, List<String>>();
                    for (PsiElement child = file.getFirstChild(); child != null; child = child.getNextSibling()) {
                        if (!(child instanceof GitignoreEntry)) {
                            continue;
                        }

                        GitignoreEntry entry = (GitignoreEntry) child;
                        String value = entry.getText();

                        List<String> matched = Glob.findAsPaths(file.getVirtualFile().getParent(), value, true);
                        if (matched.size() == 0) {
                            entries.add(entry);
                        }
                    }
                    return true;
                }
            }.process(file);
        }

        for (GitignoreEntry entry : entries) {
            problemsHolder.registerProblem(entry, GitignoreBundle.message("codeInspection.unusedEntry.message", entry.getText()));
        }

        return problemsHolder.getResultsArray();
    }
}
