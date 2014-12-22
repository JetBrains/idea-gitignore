package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.codeInspection.IgnoreCoverEntryInspection;

public class IgnoreCoverEntryInspectionTest extends GitignoreInspectionTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(IgnoreCoverEntryInspection.class);
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
