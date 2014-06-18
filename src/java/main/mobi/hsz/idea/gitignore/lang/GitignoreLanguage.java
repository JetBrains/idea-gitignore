package mobi.hsz.idea.gitignore.lang;

import com.intellij.lang.Language;

public class GitignoreLanguage extends Language {
    public static final GitignoreLanguage INSTANCE = new GitignoreLanguage();
    public static final String NAME = ".gitignore";

    private GitignoreLanguage() {
        super(NAME);
    }
}
