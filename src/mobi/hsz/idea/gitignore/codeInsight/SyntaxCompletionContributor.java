/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.codeInsight;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax;
import mobi.hsz.idea.gitignore.psi.IgnoreTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Class provides completion feature for {@link IgnoreTypes#SYNTAX} element.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.10
 */
public class SyntaxCompletionContributor extends CompletionContributor {
    public static final Iterable<LookupElementBuilder> SYNTAX_ELEMENTS = Lists.newArrayList(
            LookupElementBuilder.create("glob"), LookupElementBuilder.create("regexp")
    );

    public SyntaxCompletionContributor() {
        extend(CompletionType.BASIC,
                StandardPatterns.instanceOf(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                        PsiElement current = parameters.getOriginalPosition();
                        if (current != null && current.getParent() instanceof IgnoreSyntax && current.getPrevSibling() != null) {
                            result.addAllElements(SYNTAX_ELEMENTS);
                        }
                    }
                }
        );
    }

    /**
     * Allow autoPopup to appear after custom symbol
     */
    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return true;
    }
}
