package mobi.hsz.idea.gitignore.lexer;

import java.io.File;
import java.util.List;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.util.Glob;
import static mobi.hsz.idea.gitignore.psi.GitignoreTypes.*;
import static com.intellij.psi.TokenType.*;
%%

%{
  public GitignoreLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class GitignoreLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

CRLF            = "\r"|"\n"|"\r\n"
LINE_WS         = [\ \t\f]
WHITE_SPACE     = ({LINE_WS}*{CRLF}+)+

HEADER          = ###[^\r\n]*
SECTION         = ##[^\r\n]*
COMMENT         = #[^\r\n]*
NEGATION        = \!
SLASH           = \/
BRACKET_LEFT    = \[
BRACKET_RIGHT   = \]

FIRST_CHARACTER = [^!# ]
VALUE           = ("\\\["|"\\\]"|"\\\/"|[^\[\]\r\n\/])+

%state IN_ENTRY

%%
<YYINITIAL> {
    {WHITE_SPACE}+      { yybegin(YYINITIAL); return CRLF; }

    {HEADER}            { return HEADER; }
    {SECTION}           { return SECTION; }
    {COMMENT}           { return COMMENT; }

    {NEGATION}          { return NEGATION; }
    {FIRST_CHARACTER}   { yypushback(1); yybegin(IN_ENTRY); }

    [^]                 { return BAD_CHARACTER; }
}

<IN_ENTRY> {
  {WHITE_SPACE}+        { yybegin(YYINITIAL); return CRLF; }
  {BRACKET_LEFT}        { yybegin(IN_ENTRY); return BRACKET_LEFT; }
  {BRACKET_RIGHT}       { yybegin(IN_ENTRY); return BRACKET_RIGHT; }
  {SLASH}               { yybegin(IN_ENTRY); return SLASH; }

  {VALUE}               { yybegin(IN_ENTRY); return VALUE; }
}
