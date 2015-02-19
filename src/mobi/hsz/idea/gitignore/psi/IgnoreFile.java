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

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.tree.IFileElementType;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

/**
 * Base plugin file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class IgnoreFile extends PsiFileImpl {
    @NotNull private final Language myLanguage;
    @NotNull private final ParserDefinition myParserDefinition;

    /** Current file type. */
    @NotNull private final IgnoreFileType fileType;

    /** Builds a new instance of {@link IgnoreFile}. */
    public IgnoreFile(@NotNull FileViewProvider viewProvider, @NotNull IgnoreFileType fileType) {
        super(viewProvider);
        this.fileType = fileType;
        IgnoreLanguage language = (IgnoreLanguage) fileType.getLanguage();

        myLanguage = findLanguage(language, viewProvider);
        final ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(myLanguage);
        if (parserDefinition == null) {
            throw new RuntimeException("PsiFileBase: language.getParserDefinition() returned null for: "+myLanguage);
        }
        myParserDefinition = parserDefinition;
        final IFileElementType nodeType = parserDefinition.getFileNodeType();
        init(nodeType, nodeType);
    }

    private static Language findLanguage(Language baseLanguage, FileViewProvider viewProvider) {
        final Set<Language> languages = viewProvider.getLanguages();
        for (final Language actualLanguage : languages) {
            if (actualLanguage.isKindOf(baseLanguage)) {
                return actualLanguage;
            }
        }
        throw new AssertionError("Language " + baseLanguage + " doesn't participate in view provider " + viewProvider + ": " + new ArrayList<Language>(languages));
    }

    @Override
    @NotNull
    public final Language getLanguage() {
        return myLanguage;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }

    @NotNull
    public ParserDefinition getParserDefinition() {
        return myParserDefinition;
    }

    /**
     * Returns the file type for the file.
     *
     * @return the file type instance.
     */
    @NotNull
    @Override
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Returns @{link IgnoreFileType} string interpretation.
     *
     * @return string interpretation
     */
    @Override
    public String toString() {
        return fileType.getName();
    }
}
