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

package mobi.hsz.idea.gitignore.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.lexer.GitignoreLexerAdapter;
import mobi.hsz.idea.gitignore.parser.GitignoreParser;
import mobi.hsz.idea.gitignore.psi.GitignoreFile;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import org.jetbrains.annotations.NotNull;

public class GitignoreParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);


    // Regular comment started with #
    public static final TokenSet COMMENTS = TokenSet.create(GitignoreTypes.COMMENT);

    // Section comment started with ##
    public static final TokenSet SECTIONS = TokenSet.create(GitignoreTypes.SECTION);

    // Header comment started with ###
    public static final TokenSet HEADERS = TokenSet.create(GitignoreTypes.HEADER);

    // Negation element - ! in the beginning of the entry
    public static final TokenSet NEGATIONS = TokenSet.create(GitignoreTypes.NEGATION);

    // Brackets []
    public static final TokenSet BRACKETS = TokenSet.create(GitignoreTypes.BRACKET_LEFT, GitignoreTypes.BRACKET_RIGHT);

    // Slashes /
    public static final TokenSet SLASHES = TokenSet.create(GitignoreTypes.SLASH);

    // All values - parts of paths
    public static final TokenSet VALUES = TokenSet.create(GitignoreTypes.VALUE);


    public static final IFileElementType FILE = new IFileElementType(Language.<GitignoreLanguage>findInstance(GitignoreLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new GitignoreLexerAdapter(project);
    }

    @Override
    public PsiParser createParser(Project project) {
        return new GitignoreParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return GitignoreTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new GitignoreFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
