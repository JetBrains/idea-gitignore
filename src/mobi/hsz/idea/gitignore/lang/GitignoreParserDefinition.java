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
    public static final TokenSet HEADERS = TokenSet.create(GitignoreTypes.HEADER);
    public static final TokenSet SECTIONS = TokenSet.create(GitignoreTypes.SECTION);
    public static final TokenSet COMMENTS = TokenSet.create(GitignoreTypes.COMMENT);
    public static final TokenSet NEGATIONS = TokenSet.create(GitignoreTypes.NEGATION);
    public static final TokenSet ENTRY_FILES = TokenSet.create(GitignoreTypes.ENTRY_FILE);
    public static final TokenSet ENTRY_DIRECTORIES = TokenSet.create(GitignoreTypes.ENTRY_DIRECTORY);

    public static final IFileElementType FILE = new IFileElementType(Language.<GitignoreLanguage>findInstance(GitignoreLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new GitignoreLexerAdapter();
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
    public TokenSet getHeaderTokens() {
        return HEADERS;
    }

    @NotNull
    public TokenSet getSectionTokens() {
        return SECTIONS;
    }

    @NotNull
    public TokenSet getNegationTokens() {
        return NEGATIONS;
    }

    @NotNull
    public TokenSet getFileTokens() {
        return ENTRY_FILES;
    }

    @NotNull
    public TokenSet getDirectoryTokens() {
        return ENTRY_DIRECTORIES;
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
