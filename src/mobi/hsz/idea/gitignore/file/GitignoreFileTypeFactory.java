package mobi.hsz.idea.gitignore.file;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import org.jetbrains.annotations.NotNull;

public class GitignoreFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(GitignoreFileType.INSTANCE, new ExactFileNameMatcher(GitignoreLanguage.FILENAME));
        consumer.consume(GitignoreFileType.INSTANCE, GitignoreLanguage.EXTENSION);
    }
}
