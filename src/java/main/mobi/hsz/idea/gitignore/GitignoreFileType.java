package mobi.hsz.idea.gitignore;

import com.intellij.openapi.fileTypes.LanguageFileType;
import mobi.hsz.idea.gitignore.lang.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.GitignoreIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GitignoreFileType extends LanguageFileType {
    public static final GitignoreFileType INSTANCE = new GitignoreFileType();
    public static final String EXTENSION = "gitignore";

    private GitignoreFileType() {
        super(GitignoreLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return ".gitignore file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return ".gitignore file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GitignoreIcons.FILE;
    }
}
