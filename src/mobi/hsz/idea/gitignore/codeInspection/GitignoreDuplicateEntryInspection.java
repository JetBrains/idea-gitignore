/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

/**
 * Inspection tool that checks if entries are duplicated by others.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class GitignoreDuplicateEntryInspection extends LocalInspectionTool {
    /**
     * Reports problems at file level. Checks if entries are duplicated by other entries.
     *
     * @param file       current working file yo check
     * @param manager    {@link InspectionManager} to ask for {@link ProblemDescriptor}'s from
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return <code>null</code> if no problems found or not applicable at file level
     */
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
