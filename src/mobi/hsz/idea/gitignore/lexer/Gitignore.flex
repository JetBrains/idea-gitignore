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
  private Project project;
  private VirtualFile virtualFile;

  public GitignoreLexer(Project project, VirtualFile virtualFile) {
    this((java.io.Reader) null);
    this.project = project;
    this.virtualFile = virtualFile;
  }

  private IElementType obtainEntryType(CharSequence entry) {
    if (virtualFile != null) {
      List<File> files = Glob.find(virtualFile.getParent(), entry.toString());
      for (File file : files) {
        if (file.isFile()) {
          return ENTRY_FILE;
        }
      }
      if (files.size() > 0) {
        return ENTRY_DIRECTORY;
      }
    }
    return ENTRY_FILE;
  }
%}
 
%public
%class GitignoreLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

CRLF="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}*{CRLF}+)+
 
HEADER=###[^\r\n]*
SECTION=##[^\r\n]*
COMMENT=#[^\r\n]*
NEGATION=[!]
SLASH="/"

FIRST_CHARACTER=[^!# ]

%state IN_ENTRY
%state WAITING_VALUE
 
%%
<YYINITIAL> {
    {WHITE_SPACE}+      { yybegin(YYINITIAL); return WHITE_SPACE; }
 
    {HEADER}            { return HEADER; }
    {SECTION}           { return SECTION; }
    {COMMENT}           { return COMMENT; }
 
    {NEGATION}          { return NEGATION; }
    {FIRST_CHARACTER}   { yypushback(1); yybegin(IN_ENTRY); }

    [^]                 { return BAD_CHARACTER; }
}

<IN_ENTRY> {
  {WHITE_SPACE}+        { yybegin(YYINITIAL); return WHITE_SPACE; }
  .+{SLASH}             { yybegin(IN_ENTRY); return ENTRY_DIRECTORY; }
  .+                    { yybegin(IN_ENTRY); return obtainEntryType(yytext()); }
}
 
<WAITING_VALUE> {
  {WHITE_SPACE}+        { yybegin(YYINITIAL); return WHITE_SPACE; }
  [^]                   { return BAD_CHARACTER; }
}
