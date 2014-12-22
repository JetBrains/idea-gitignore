/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.inspections;

import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.lang.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.codeInspection.IgnoreRelativeEntryInspection;

public class IgnoreRelativeEntryInspectionTest extends GitignoreInspectionTestCase {
    private static final String FILENAME = GitignoreLanguage.INSTANCE.getFilename();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(IgnoreRelativeEntryInspection.class);
    }

    public void testSimpleCase() throws Exception {
        doHighlightingFileTest();
    }

    public void testQuickFix() throws Exception {
        String name = getTestName(true);
        for (int i = 1; i < 6; i++) {
            myFixture.configureByFile(name + i + FILENAME);
            myFixture.testHighlighting(true, false, true);
            myFixture.launchAction(myFixture.findSingleIntention(IgnoreBundle.message("quick.fix.relative.entry")));
            myFixture.checkResultByFile(name + i + "-after" + FILENAME);
        }
    }

}
