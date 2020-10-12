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

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * QuickFix action that removes relative parts of the entry
 * {@link IgnoreRelativeEntryInspection}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class IgnoreRelativeEntryFix extends LocalQuickFixOnPsiElement {
    /**
     * Builds a new instance of {@link IgnoreRelativeEntryFix}.
     *
     * @param entry an element that will be handled with QuickFix
     */
    public IgnoreRelativeEntryFix(@NotNull IgnoreEntry entry) {
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
        return IgnoreBundle.message("quick.fix.relative.entry");
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
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        if (startElement instanceof IgnoreEntry) {
            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document != null) {
                int start = startElement.getStartOffsetInParent();
                String text = startElement.getText();
                String fixed = getFixedPath(text);
                document.replaceString(start, start + text.length(), fixed);
            }
        }
    }

    /**
     * Removes relative parts from the given path.
     *
     * @param path element
     * @return fixed path
     */
    private String getFixedPath(String path) {
        path = path.replaceAll("\\/", "/").replaceAll("\\\\\\.", ".");
        try {
            path = new URI(path).normalize().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return path.replaceAll("/\\.{1,2}/", "/").replaceAll("^\\.{0,2}/", "");
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
