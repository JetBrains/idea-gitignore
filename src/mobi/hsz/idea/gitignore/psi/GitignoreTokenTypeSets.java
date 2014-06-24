package mobi.hsz.idea.gitignore.psi;

import com.intellij.psi.tree.TokenSet;

public interface GitignoreTokenTypeSets extends GitignoreTypes {

    /** Regular comment started with # */
    TokenSet COMMENT_SET = TokenSet.create(COMMENT);

    /** Section comment started with ## */
    TokenSet SECTION_SET = TokenSet.create(SECTION);

    /** Header comment started with ### */
    TokenSet HEADER_SET = TokenSet.create(HEADER);

    /** Negation element - ! in the beginning of the entry */
    TokenSet NEGATION_SET = TokenSet.create(NEGATION);

    /** Regular entry */
    TokenSet ENTRY_FILE_SET = TokenSet.create(ENTRY_FILE);

    /** Directory entry - ends with / */
    TokenSet ENTRY_DIRECTORY_SET = TokenSet.create(ENTRY_DIRECTORY);

}
