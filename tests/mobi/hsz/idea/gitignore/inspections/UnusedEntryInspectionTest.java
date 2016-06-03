package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.codeInspection.IgnoreUnusedEntryInspection;

public class UnusedEntryInspectionTest extends InspectionTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(IgnoreUnusedEntryInspection.class);
    }

    public void testUnusedFile() throws Exception {
//        doHighlightingTest();
    }

    public void testUnusedDirectory() throws Exception {
        doHighlightingTest();
    }
}
