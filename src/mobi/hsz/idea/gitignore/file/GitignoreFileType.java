package mobi.hsz.idea.gitignore.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GitignoreFileType extends LanguageFileType {
    public static final GitignoreFileType INSTANCE = new GitignoreFileType();

    private GitignoreFileType() {
        super(GitignoreLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return GitignoreLanguage.NAME + " file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return GitignoreLanguage.NAME + " file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return GitignoreLanguage.EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.FILE;
    }
}
