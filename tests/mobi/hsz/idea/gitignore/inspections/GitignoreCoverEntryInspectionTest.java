package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.codeInspection.GitignoreCoverEntryInspection;

public class GitignoreCoverEntryInspectionTest extends GitignoreInspectionTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(GitignoreCoverEntryInspection.class);
    }

    public void testEmptyEntries() throws Exception {
        doHighlightingTest();
    }

    public void testDuplicates() throws Exception {
        doHighlightingTest();
    }

    public void testCovering() throws Exception {
        doHighlightingTest();
    }
}
