package mobi.hsz.idea.gitignore.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GitignoreFile extends PsiFileBase {
    public GitignoreFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, GitignoreLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return GitignoreFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return GitignoreLanguage.NAME + " file";
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
