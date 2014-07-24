package mobi.hsz.idea.gitignore.refactoring;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RenameTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testRenameFile() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("dir").createChildData(this, "file.txt");
        doTest("*/fil<caret>e.txt", "newFile.txt", "dir/newFile.txt");
    }

    public void testRenameDirectory() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("dir").createChildData(this, "file.txt");
        doTest("./di<caret>r/file.txt", "newDir", "./newDir/file.txt");
    }

    public void _testRenameMultiResolvedFile() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("dir1").createChildData(this, "file.txt");
        myFixture.getTempDirFixture().findOrCreateDir("dir2").createChildData(this, "file.txt");
        doTest("dir*/fil<caret>e.txt", "newFile", "*/fi<caret>le.txt");
    }
    
    public void testRenameInNegationEntry() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("dir").createChildData(this, "file.txt");
        doTest("!di<caret>r/file.txt", "newDir", "!newDir/file.txt");
    }

    private void doTest(@NotNull String beforeText, @NotNull String newName, @NotNull String afterText) {
        myFixture.configureByText(GitignoreFileType.INSTANCE, beforeText);
        myFixture.renameElementAtCaret(newName);
        myFixture.checkResult(afterText);
    }
}
