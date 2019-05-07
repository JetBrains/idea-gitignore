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

package mobi.hsz.idea.gitignore.codeInsight;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class provides completion feature for {@link mobi.hsz.idea.gitignore.psi.IgnoreTypes#SYNTAX} element.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0
 */
public class SyntaxCompletionContributor extends CompletionContributor {
    /** Allowed values for the completion. */
    @NotNull
    private static final List<LookupElementBuilder> SYNTAX_ELEMENTS = ContainerUtil.newArrayList();

    static {
        for (IgnoreBundle.Syntax syntax : IgnoreBundle.Syntax.values()) {
            SYNTAX_ELEMENTS.add(LookupElementBuilder.create(syntax.toString().toLowerCase()));
        }
    }

    /** Constructor. */
    public SyntaxCompletionContributor() {
        extend(CompletionType.BASIC,
                StandardPatterns.instanceOf(PsiElement.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        PsiElement current = parameters.getPosition();
                        if (current.getParent() instanceof IgnoreSyntax && current.getPrevSibling() != null) {
                            result.addAllElements(SYNTAX_ELEMENTS);
                        }
                    }
                }
        );
    }
}
