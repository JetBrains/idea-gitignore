/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Inspection tool that checks if entries are covered by others.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreCoverEntryInspection extends LocalInspectionTool {
    /**
     * Reports problems at file level. Checks if entries are covered by other entries.
     *
     * @param file       current working file to check
     * @param manager    {@link InspectionManager} to ask for {@link ProblemDescriptor}'s from
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return <code>null</code> if no problems found or not applicable at file level
     */
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(file instanceof IgnoreFile)) {
            return null;
        }

        final VirtualFile virtualFile = file.getVirtualFile();
        final VirtualFile contextDirectory = virtualFile.getParent();
        if (contextDirectory == null) {
            return null;
        }

        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<Pair<IgnoreEntry, IgnoreEntry>> entries = ContainerUtil.newArrayList();
        final Map<IgnoreEntry, Set<String>> map = ContainerUtil.newHashMap();

        file.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                Set<String> matched = ContainerUtil.newHashSet(Glob.findAsPaths(contextDirectory, entry, true));

                for (IgnoreEntry recent : map.keySet()) {
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

        for (Pair<IgnoreEntry, IgnoreEntry> pair : entries) {
            problemsHolder.registerProblem(pair.second, message(pair.first, virtualFile, isOnTheFly),
                    new IgnoreRemoveEntryFix(pair.second));
        }

        return problemsHolder.getResultsArray();
    }

    /**
     * Helper for inspection message generating.
     *
     * @param coveringEntry entry that covers message related
     * @param virtualFile   current working file
     * @param onTheFly      true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return generated message {@link String}
     */
    private static String message(@NotNull IgnoreEntry coveringEntry, @NotNull VirtualFile virtualFile, boolean onTheFly) {
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (onTheFly || document == null) {
            return IgnoreBundle.message("codeInspection.coverEntry.message", "'" + coveringEntry.getText() + "'");
        }
        
        int startOffset = coveringEntry.getTextRange().getStartOffset();
        return IgnoreBundle.message("codeInspection.coverEntry.message",
                "<a href=\"" + virtualFile.getUrl() + "#" + startOffset + "\">" + coveringEntry.getText() + "</a>");
    }

    /**
     * Forces checking every entry in checked file.
     *
     * @return <code>true</code>
     */
    @Override
    public boolean runForWholeFile() {
        return true;
    }
}
