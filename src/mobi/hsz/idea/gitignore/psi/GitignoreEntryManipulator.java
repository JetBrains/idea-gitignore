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

package mobi.hsz.idea.gitignore.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Entry manipulator.
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @since 0.5
 */
public class GitignoreEntryManipulator extends AbstractElementManipulator<GitignoreEntry> {
    /**
     * Changes the element's text to a new value
     *
     * @param element element to be changed
     * @param range range within the element
     * @param newContent new element text
     * @return changed element
     * @throws IncorrectOperationException if something goes wrong
     */
    @Override
    public GitignoreEntry handleContentChange(@NotNull GitignoreEntry entry, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        GitignoreFile file = (GitignoreFile) PsiFileFactory.getInstance(entry.getProject())
                .createFileFromText(GitignoreLanguage.FILENAME, GitignoreFileType.INSTANCE, textRange.replace(entry.getText(), s));
        GitignoreEntry newEntry = PsiTreeUtil.findChildOfType(file, GitignoreEntry.class);
        assert newEntry != null;
        return (GitignoreEntry) entry.replace(newEntry);
    }

    /**
     * Returns range of the entry. Skips negation element.
     *
     * @param element element to be changed
     * @return range
     */
    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull GitignoreEntry element) {
        GitignoreNegation negation = element.getNegation();
        if (negation != null) {
            return TextRange.create(negation.getStartOffsetInParent() + negation.getTextLength(), element.getTextLength());
        }
        return super.getRangeInElement(element);
    }
}
