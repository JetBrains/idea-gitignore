package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.codeInspection.GitignoreDuplicateEntryInspection;

public class GitignoreDuplicateEntryInspectionTest extends GitignoreInspectionTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(GitignoreDuplicateEntryInspection.class);
    }

    public void testSimpleCase() throws Exception {
        doHighlightingFileTest();
    }
}
