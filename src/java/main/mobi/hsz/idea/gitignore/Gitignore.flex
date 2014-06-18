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
FIRST_VALUE_CHARACTER=[^ \n\r\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER=[^\n\r\f\\] | "\\"{CRLF} | "\\".
HEADER="###"[^\r\n]*
SECTION="##"[^#\r\n]*
COMMENT="#"[^#\r\n]*
SEPARATOR=[:=]
KEY_CHARACTER=[^:=\ \n\r\t\f\\] | "\\"{CRLF} | "\\".

%state WAITING_VALUE

%%

<YYINITIAL> {HEADER}                                        { yybegin(YYINITIAL); return GitignoreTypes.HEADER; }

<YYINITIAL> {SECTION}                                       { yybegin(YYINITIAL); return GitignoreTypes.SECTION; }

<YYINITIAL> {COMMENT}                                       { yybegin(YYINITIAL); return GitignoreTypes.COMMENT; }

<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return GitignoreTypes.KEY; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return GitignoreTypes.SEPARATOR; }

<WAITING_VALUE> {CRLF}                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { yybegin(YYINITIAL); return GitignoreTypes.VALUE; }

{CRLF}                                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

{WHITE_SPACE}+                                              { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }