package mobi.hsz.idea.gitignore.lang;

import com.intellij.lang.ASTNode;
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
<<<<<<< HEAD
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(GitignoreTypes.COMMENT);
    private static final IFileElementType FILE = new IFileElementType(GitignoreLanguage.INSTANCE);
=======

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
>>>>>>> brace-matcher

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
<<<<<<< HEAD
=======
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
    public TokenSet getBracketTokens() {
        return BRACKETS;
    }

    @NotNull
    public TokenSet getSlashTokens() {
        return SLASHES;
    }

    @NotNull
    public TokenSet getValueTokens() {
        return VALUES;
    }

    @NotNull
>>>>>>> brace-matcher
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
