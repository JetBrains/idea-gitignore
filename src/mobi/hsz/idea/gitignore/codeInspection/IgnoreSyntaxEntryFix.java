/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * QuickFix action that invokes {@link mobi.hsz.idea.gitignore.codeInsight.SyntaxCompletionContributor}
 * on the given {@link IgnoreSyntax} element.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0
 */
public class IgnoreSyntaxEntryFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    /**
     * Builds a new instance of {@link IgnoreSyntaxEntryFix}.
     *
     * @param syntax an element that will be handled with QuickFix
     */
    public IgnoreSyntaxEntryFix(@NotNull IgnoreSyntax syntax) {
        super(syntax);
    }

    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    @NotNull
    @Override
    public String getText() {
        return IgnoreBundle.message("quick.fix.syntax.entry");
    }

    /**
     * Handles QuickFix action invoked on {@link IgnoreSyntax}.
     *
     * @param project      the {@link Project} containing the working file
     * @param file         the {@link PsiFile} containing handled entry
     * @param startElement the {@link IgnoreSyntax} that will be selected and replaced
     * @param endElement   the {@link PsiElement} which is ignored in invoked action
     */
    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (startElement instanceof IgnoreSyntax) {
            PsiElement value = ((IgnoreSyntax) startElement).getValue();
            if (editor != null) {
                editor.getSelectionModel().setSelection(
                        value.getTextOffset(),
                        value.getTextOffset() + value.getTextLength()
                );
            }
            new CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor);
        }
    }

    /**
     * Run in read action because of completion invoking.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return IgnoreBundle.message("codeInspection.group");
    }
}
