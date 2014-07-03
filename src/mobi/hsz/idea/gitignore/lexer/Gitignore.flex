package mobi.hsz.idea.gitignore.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static mobi.hsz.idea.gitignore.psi.GitignoreTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%class GitignoreLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF        = \n|\r|\r\n
WHITE_SPACE = [\ \t\f]

COMMENT     = "#"[^#\r\n][^\r\n]*
SECTION     = "#"{COMMENT}
HEADER      = "##"{COMMENT}
CHARACTER   = [^\n\r\f]
NEGATION    = [!]
SLASH       = [/]

%state IN_ENTRY
%state WAITING_VALUE

%%

<YYINITIAL> {
  {WHITE_SPACE}+      { yybegin(YYINITIAL); return WHITE_SPACE; }

  {HEADER}            { yybegin(YYINITIAL); return HEADER; }
  {SECTION}           { yybegin(YYINITIAL); return SECTION; }
  {COMMENT}           { yybegin(YYINITIAL); return COMMENT; }
  {NEGATION}          { yybegin(IN_ENTRY); return NEGATION; }
  {CHARACTER}         { yypushback(1); yybegin(IN_ENTRY); }

  .                   { yybegin(YYINITIAL); return BAD_CHARACTER; }
}

<IN_ENTRY> {
  {CHARACTER}+{SLASH} { yybegin(WAITING_VALUE); return ENTRY_DIRECTORY; }
  {CHARACTER}+        { yybegin(WAITING_VALUE); return ENTRY_FILE; }
  {CRLF}              { yybegin(YYINITIAL); return CRLF; }
}

<WAITING_VALUE> {
  {CRLF}              { yybegin(YYINITIAL); return CRLF; }
  {WHITE_SPACE}+      { yybegin(WAITING_VALUE); return WHITE_SPACE; }
}

{CRLF}                { yybegin(YYINITIAL); return CRLF; }
{WHITE_SPACE}+        { yybegin(YYINITIAL); return WHITE_SPACE; }
.                     { return BAD_CHARACTER; }
