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
ENTRY_FILE=[^\n\r\f\\] | "\\"{CRLF} | "\\".
ENTRY_DIRECTORY={ENTRY_FILE}*[/]

SEPARATOR=[:=]
KEY_CHARACTER=[^:=\ \n\r\t\f\\] | "\\"{CRLF} | "\\".

%state WAITING_VALUE

%%

<YYINITIAL> {HEADER}                                        { yybegin(YYINITIAL); return GitignoreTypes.HEADER; }

<YYINITIAL> {SECTION}                                       { yybegin(YYINITIAL); return GitignoreTypes.SECTION; }

<YYINITIAL> {COMMENT}                                       { yybegin(YYINITIAL); return GitignoreTypes.COMMENT; }

<YYINITIAL> {ENTRY_FILE}                                    { yybegin(YYINITIAL); return GitignoreTypes.ENTRY_FILE; }

<YYINITIAL> {ENTRY_DIRECTORY}                               { yybegin(YYINITIAL); return GitignoreTypes.ENTRY_DIRECTORY; }

//<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return GitignoreTypes.KEY; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return GitignoreTypes.SEPARATOR; }

<WAITING_VALUE> {CRLF}                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { yybegin(YYINITIAL); return GitignoreTypes.VALUE; }

{CRLF}                                                      { yybegin(YYINITIAL); return GitignoreTypes.CRLF; }

{WHITE_SPACE}+                                              { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

.                                                           { return TokenType.BAD_CHARACTER; }