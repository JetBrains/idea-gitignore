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

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.gitignore.GitignoreTypes;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.npmignore.NpmignoreTypes;
import org.jetbrains.annotations.NotNull;

/**
 * QuickFix action that removes specified entry handled by code inspections like
 * {@link IgnoreCoverEntryInspection},
 * {@link IgnoreDuplicateEntryInspection},
 * {@link IgnoreUnusedEntryInspection}.
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
public class IgnoreRemoveEntryFix extends LocalQuickFixOnPsiElement {
    /**
     * Builds a new instance of {@link IgnoreRemoveEntryFix}.
     *
     * @param entry an element that will be handled with QuickFix
     */
    public IgnoreRemoveEntryFix(@NotNull IgnoreEntry entry) {
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
        return IgnoreBundle.message("quick.fix.remove.entry");
    }

    /**
     * Handles QuickFix action invoked on {@link IgnoreEntry}.
     *
     * @param project      the {@link Project} containing the working file
     * @param psiFile      the {@link PsiFile} containing handled entry
     * @param startElement the {@link IgnoreEntry} that will be removed
     * @param endElement   the {@link PsiElement} which is ignored in invoked action
     */
    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (startElement instanceof IgnoreEntry) {
            removeCrlf(startElement, GitignoreTypes.CRLF);
            removeCrlf(startElement, NpmignoreTypes.CRLF);
            startElement.delete();
        }
    }

    /**
     * Shorthand method for removing CRLF element.
     *
     * @param startElement working PSI element
     * @param crlf         CRLF element to remove
     */
    private void removeCrlf(PsiElement startElement, IElementType crlf) {
        ASTNode node = TreeUtil.findSibling(startElement.getNode(), crlf);
        if (node == null) {
            node = TreeUtil.findSiblingBackward(startElement.getNode(), crlf);
        }
        if (node != null) {
            node.getPsi().delete();
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
        return IgnoreBundle.message("codeInspection.group");
    }
}
