package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitignoreDuplicateEntryInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<GitignoreEntry> entries = new ArrayList<GitignoreEntry>();

        if (file instanceof GitignoreFile) {
            new Processor<PsiFile>() {
                @Override
                public boolean process(PsiFile file) {
                    Map<String, GitignoreEntry> map = new HashMap<String, GitignoreEntry>();
                    for (PsiElement child = file.getFirstChild(); child != null; child = child.getNextSibling()) {
                        if (!(child instanceof GitignoreEntry)) {
                            continue;
                        }
                        GitignoreEntry entry = (GitignoreEntry) child;
                        String value = entry.getText();

                        if (map.containsKey(value)) {
                            entries.add(entry);
                        } else {
                            map.put(value, entry);
                        }
                    }

                    return true;
                }
            }.process(file);
        }

        for (GitignoreEntry entry : entries) {
            problemsHolder.registerProblem(entry, GitignoreBundle.message("codeInspection.duplicateEntry.message", entry.getText()));
        }

        return problemsHolder.getResultsArray();
    }
}
