/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Inspection tool that checks if syntax entry has correct value.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreSyntaxEntryInspection extends LocalInspectionTool {
    /**
     * Checks if syntax entry has correct value.
     *
     * @param holder     where visitor will register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return not-null visitor for this inspection
     */
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new IgnoreVisitor() {
            @Override
            public void visitSyntax(@NotNull IgnoreSyntax syntax) {
                IgnoreLanguage language = (IgnoreLanguage) syntax.getContainingFile().getLanguage();
                if (!language.isSyntaxSupported()) {
                    return;
                }

                String value = syntax.getValue().getText();
                for (IgnoreBundle.Syntax s : IgnoreBundle.Syntax.values()) {
                    if (s.toString().equals(value)) {
                        return;
                    }
                }

                holder.registerProblem(syntax, IgnoreBundle.message("codeInspection.syntaxEntry.message"),
                        new IgnoreSyntaxEntryFix(syntax));
            }
        };
    }
}
