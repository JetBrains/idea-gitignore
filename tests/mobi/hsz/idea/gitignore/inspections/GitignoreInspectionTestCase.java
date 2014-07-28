package mobi.hsz.idea.gitignore.inspections;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import org.jetbrains.annotations.NotNull;

import java.io.File;

abstract public class GitignoreInspectionTestCase extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return new File("testData/inspections/" + name()).getAbsolutePath();
    }

    private String name() {
        return StringUtil.decapitalize(StringUtil.trimEnd(StringUtil.trimStart(getClass().getSimpleName(), "Gitignore"), "InspectionTest"));
    }

    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }

    protected void doHighlightingTest() {
        myFixture.copyDirectoryToProject(getTestName(true), getTestName(true));
        myFixture.testHighlighting(true, false, true, getTestName(true) + "/" + GitignoreLanguage.FILENAME);
    }
    
    protected void doHighlightingFileTest() {
        myFixture.configureByFile(getTestName(true) + GitignoreLanguage.FILENAME);
        myFixture.testHighlighting(true, false, true);
    }
    
    protected void doHighlightingFileTestWithQuickFix(@NotNull String quickFixName) {
        myFixture.configureByFile(getTestName(true) + GitignoreLanguage.FILENAME);
        myFixture.testHighlighting(true, false, true);
        myFixture.launchAction(myFixture.findSingleIntention(quickFixName));
        myFixture.checkResultByFile(getTestName(true) + "-after" + GitignoreLanguage.FILENAME);
    }
}
