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

package mobi.hsz.idea.gitignore.highlighter.dockerignore;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.highlighter.IgnoreHighlighterColors;
import mobi.hsz.idea.gitignore.lang.dockerignore.DockerignoreParserDefinition;
import mobi.hsz.idea.gitignore.lexer.dockerignore.DockerignoreLexerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Syntax highlighter definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
public class DockerignoreHighlighter extends SyntaxHighlighterBase {
    /** Attributes map. */
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = ContainerUtil.newHashMap();

    /** Binds parser definitions with highlighter colors. */
    static {
        SyntaxHighlighterBase.fillMap(ATTRIBUTES, DockerignoreParserDefinition.COMMENTS, IgnoreHighlighterColors.COMMENT_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.SECTIONS, IgnoreHighlighterColors.SECTION_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.HEADERS, IgnoreHighlighterColors.HEADER_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.NEGATIONS, IgnoreHighlighterColors.NEGATION_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.BRACKETS, IgnoreHighlighterColors.BRACKET_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.SLASHES, IgnoreHighlighterColors.SLASH_ATTR_KEY);
        fillMap(ATTRIBUTES, DockerignoreParserDefinition.VALUES, IgnoreHighlighterColors.VALUE_ATTR_KEY);
    }

    /** Current project. */
    private final Project project;

    /** Current file. */
    private final VirtualFile virtualFile;

    /** Builds a new instance of {@link DockerignoreHighlighter}. */
    public DockerignoreHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        this.project = project;
        this.virtualFile = virtualFile;
    }

    /**
     * Creates lexer adapter.
     *
     * @return lexer adapter
     */
    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new DockerignoreLexerAdapter(project, virtualFile);
    }

    /**
     * Gets highlighter text {@link com.intellij.openapi.editor.colors.TextAttributesKey} list using {@link com.intellij.psi.tree.IElementType} token.
     *
     * @param tokenType element type
     * @return attributes list
     */
    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }
}
