package mobi.hsz.idea.gitignore.lang;

import com.intellij.lang.Language;

public class GitignoreLanguage extends Language {
    public static final GitignoreLanguage INSTANCE = new GitignoreLanguage();
    public static final String NAME = "Gitignore";
    public static final String EXTENSION = "gitignore";
    public static final String FILENAME = "." + EXTENSION;

    private GitignoreLanguage() {
        super(NAME);
    }
}
