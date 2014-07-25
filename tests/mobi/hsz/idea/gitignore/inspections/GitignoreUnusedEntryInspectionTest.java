package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.codeInspection.GitignoreUnusedEntryInspection;

public class GitignoreUnusedEntryInspectionTest extends GitignoreInspectionTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(GitignoreUnusedEntryInspection.class);
    }

    public void testUnusedFile() throws Exception {
        doHighlightingTest();
    }

    public void testUnusedDirectory() throws Exception {
        doHighlightingTest();
    }
}
