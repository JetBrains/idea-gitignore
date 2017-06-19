package mobi.hsz.idea.gitignore.completion;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CompletionTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testSimple() {
        myFixture.getTempDirFixture().createFile("fileName.txt");
        doTest("fileN<caret>", "fileName.txt<caret>");
    }

    public void testCurrentDirectoryAlias() throws IOException {
        new WriteCommandAction(getProject()) {
            @Override
            protected void run(@NotNull Result result) {
                myFixture.getTempDirFixture().createFile("fileName.txt");
            }
        }.execute();

        doTest("./fileN<caret>", "./fileName.txt<caret>");
    }

    public void testInHiddenDirectory() throws IOException {
        new WriteCommandAction(getProject()) {
            @Override
            protected void run(@NotNull Result result) throws IOException {
                myFixture.getTempDirFixture().findOrCreateDir(".hidden").createChildData(this, "fileName.txt");
            }
        }.execute();

        doTest(".hidden/fileN<caret>", ".hidden/fileName.txt<caret>");
    }

    public void testInGlobDirectory() throws IOException {
        new WriteCommandAction(getProject()) {
            @Override
            protected void run(@NotNull Result result) throws IOException {
                myFixture.getTempDirFixture().findOrCreateDir("glob1").createChildData(this, "fileName1.txt");
                myFixture.getTempDirFixture().findOrCreateDir("glob2").createChildData(this, "fileName2.txt");
            }
        }.execute();

        doTestVariants("*/fileN<caret>", "fileName1.txt", "fileName2.txt");
    }

    public void testNegation() throws IOException {
        myFixture.getTempDirFixture().createFile("fileName.txt");
        doTest("!fileNa<caret>", "!fileName.txt<caret>");
    }

    private void doTest(@NotNull String beforeText, @NotNull String afterText) {
        myFixture.configureByText(GitFileType.INSTANCE, beforeText);
        myFixture.completeBasic();
        myFixture.checkResult(afterText);
    }

    private void doTestVariants(@NotNull String beforeText, String... variants) {
        myFixture.configureByText(GitFileType.INSTANCE, beforeText);
        myFixture.completeBasic();
        assertContainsElements(myFixture.getLookupElementStrings(), variants);
    }
}
