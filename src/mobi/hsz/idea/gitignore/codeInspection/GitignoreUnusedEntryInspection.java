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

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.actions.GitignoreRemoveEntryFix;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreVisitor;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Inspection tool that checks if entries are unused - does not cover any file or directory.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class GitignoreUnusedEntryInspection extends LocalInspectionTool {
    /**
     * Checks if entries are related to any file.
     *
     * @param holder     where visitor will register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return not-null visitor for this inspection
     */
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GitignoreVisitor() {
            @Override
            public void visitEntry(@NotNull GitignoreEntry entry) {
                PsiReference[] references = entry.getReferences();
                boolean resolved = true;
                int previous = Integer.MAX_VALUE;
                for (PsiReference reference : references) {
                    if (reference instanceof FileReferenceOwner) {
                        PsiFileReference fileReference = (PsiFileReference) reference;
                        ResolveResult[] result = fileReference.multiResolve(false);
                        resolved = result.length > 0 || (previous > 0 && reference.getCanonicalText().endsWith("/*"));
                        previous = result.length;
                    }
                    if (!resolved) {
                        break;
                    }
                }

                if (!resolved) {
                    if (!Utils.isEntryExcluded(entry, holder.getProject())) {
                        holder.registerProblem(entry, GitignoreBundle.message("codeInspection.unusedEntry.message"), new GitignoreRemoveEntryFix(entry));
                    }
                }

                super.visitEntry(entry);
            }
        };
    }
}
