package mobi.hsz.idea.gitignore;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import mobi.hsz.idea.gitignore.psi.GitignoreTypes;
import com.intellij.psi.TokenType;

%%

%class GitignoreLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]

COMMENT="#"[^#\r\n]*
SECTION="#"{COMMENT}
HEADER="##"{COMMENT}
CHARACTER=[^\n\r\f\\] | "\\"{CRLF} | "\\".

%state WAITING_VALUE

%%

<YYINITIAL> {HEADER}                                        { yybegin(YYINITIAL); return GitignoreTypes.HEADER; }

<YYINITIAL> {SECTION}                                       { yybegin(YYINITIAL); return GitignoreTypes.SECTION; }

<YYINITIAL> {COMMENT}                                       { yybegin(YYINITIAL); return GitignoreTypes.COMMENT; }

<YYINITIAL> {CHARACTER}*[/]                                 { yybegin(YYINITIAL); return GitignoreTypes.ENTRY_DIRECTORY; }

<YYINITIAL> {CHARACTER}*                                    { yybegin(YYINITIAL); return GitignoreTypes.ENTRY_FILE; }

<WAITING_VALUE> {CRLF}                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

{CRLF}                                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

{WHITE_SPACE}+                                              { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }