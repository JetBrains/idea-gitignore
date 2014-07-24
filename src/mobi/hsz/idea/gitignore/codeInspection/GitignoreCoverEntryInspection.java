package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Pair;
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

public class GitignoreCoverEntryInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<Pair<GitignoreEntry, GitignoreEntry>> entries = new ArrayList<Pair<GitignoreEntry, GitignoreEntry>>();

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

                        for (GitignoreEntry recent : map.keySet()) {
                            List<String> recentValues = map.get(recent);
                            if (recentValues.size() == 0 || matched.size() == 0) {
                                continue;
                            }
                            if (recentValues.containsAll(matched)) {
                                entries.add(Pair.create(recent, entry));
                            } else if (matched.containsAll(recentValues)) {
                                entries.add(Pair.create(entry, recent));
                            }
                        }

                        map.put(entry, matched);
                    }
                    return true;
                }
            }.process(file);
        }

        for (Pair<GitignoreEntry, GitignoreEntry> pair: entries) {
            GitignoreEntry key = pair.getFirst();
            GitignoreEntry value = pair.getSecond();
            problemsHolder.registerProblem(value, GitignoreBundle.message("codeInspection.coverEntry.message", value.getText(), key.getText()));
        }

        return problemsHolder.getResultsArray();
    }
}
