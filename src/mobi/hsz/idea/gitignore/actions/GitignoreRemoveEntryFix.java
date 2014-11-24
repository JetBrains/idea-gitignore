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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.TreeUtil;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import org.jetbrains.annotations.NotNull;

/**
 * QuickFix action that removes specified entry handled by code inspections like
 * {@link mobi.hsz.idea.gitignore.codeInspection.GitignoreCoverEntryInspection},
 * {@link mobi.hsz.idea.gitignore.codeInspection.GitignoreDuplicateEntryInspection},
 * {@link mobi.hsz.idea.gitignore.codeInspection.GitignoreUnusedEntryInspection}.
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class GitignoreRemoveEntryFix extends LocalQuickFixOnPsiElement {
    /**
     * Builds a new instance of {@link GitignoreRemoveEntryFix}.
     *
     * @param entry an element that will be handled with QuickFix
     */
    public GitignoreRemoveEntryFix(@NotNull GitignoreEntry entry) {
        super(entry);
    }

    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    @NotNull
    @Override
    public String getText() {
        return GitignoreBundle.message("quick.fix.remove.entry");
    }

    /**
     * Handles QuickFix action invoked on {@link GitignoreEntry}.
     *
     * @param project      the {@link Project} containing the working file
     * @param psiFile      the {@link PsiFile} containing handled entry
     * @param startElement the {@link GitignoreEntry} that will be removed
     * @param endElement   the {@link PsiElement} which is ignored in invoked action
     */
    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (startElement instanceof GitignoreEntry) {
            ASTNode crlf = TreeUtil.findSibling(startElement.getNode(), GitignoreTypes.CRLF);
            if (crlf == null) {
                crlf = TreeUtil.findSiblingBackward(startElement.getNode(), GitignoreTypes.CRLF);
            }
            if (crlf != null) {
                crlf.getPsi().delete();
            }
            startElement.delete();
        }
    }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return GitignoreBundle.message("codeInspection.group");
    }
}
