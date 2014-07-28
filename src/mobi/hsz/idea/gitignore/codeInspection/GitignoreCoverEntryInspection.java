package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.actions.GitignoreRemoveEntryFix;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreVisitor;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitignoreCoverEntryInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        final VirtualFile virtualFile = file.getVirtualFile();
        final VirtualFile contextDirectory = virtualFile.getParent();
        if (contextDirectory == null) {
            return null;
        }

        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<Pair<GitignoreEntry, GitignoreEntry>> entries = ContainerUtil.newArrayList();
        final Map<GitignoreEntry, Set<String>> map = ContainerUtil.newHashMap();

        file.acceptChildren(new GitignoreVisitor() {
            @Override
            public void visitEntry(@NotNull GitignoreEntry entry) {
                String value = entry.getText();

                Set<String> matched = ContainerUtil.newHashSet(Glob.findAsPaths(contextDirectory, value, true));

                for (GitignoreEntry recent : map.keySet()) {
                    Set<String> recentValues = map.get(recent);
                    if (recentValues.isEmpty() || matched.isEmpty()) {
                        continue;
                    }

                    /**
                     * TODO:
                     * It should choose highlighting element smarter.
                     * In the ideal world more concrete pattern should be highlighted.
                     * E.g. given following structure:
                     * root
                     *   - dir
                     *     - file.txt
                     *   
                     * 'dir/f*' and dir/* match the same set of files, so at the moment the _latest_in_file_ will 
                     * be always highlighted. Meanwhile 'dir/*' is wider so it should have priority regardless of 
                     * position in file.
                     */
                    if (recentValues.containsAll(matched)) {
                        entries.add(Pair.create(recent, entry));
                    } else if (matched.containsAll(recentValues)) {
                        entries.add(Pair.create(entry, recent));
                    }
                }

                map.put(entry, matched);
            }
        });

        for (Pair<GitignoreEntry, GitignoreEntry> pair : entries) {
            problemsHolder.registerProblem(pair.second, message(pair.first, virtualFile, isOnTheFly),
                    new GitignoreRemoveEntryFix(pair.second));
        }

        return problemsHolder.getResultsArray();
    }

    private static String message(@NotNull GitignoreEntry coveringEntry, @NotNull VirtualFile virtualFile, boolean onTheFly) {
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (onTheFly || document == null) {
            return GitignoreBundle.message("codeInspection.coverEntry.message", "'" + coveringEntry.getText() + "'"); 
        }
        
        int startOffset = coveringEntry.getTextRange().getStartOffset();
        return GitignoreBundle.message("codeInspection.coverEntry.message", 
                "<a href=\"" + virtualFile.getUrl() + "#" + startOffset + "\">" + coveringEntry.getText() + "</a>");
    }

    @Override
    public boolean runForWholeFile() {
        return true;
    }
}
