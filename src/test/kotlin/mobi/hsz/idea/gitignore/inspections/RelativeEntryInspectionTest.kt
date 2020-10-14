// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.inspections

import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.codeInspection.IgnoreRelativeEntryInspection

class RelativeEntryInspectionTest : InspectionTestCase() {

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        myFixture.enableInspections(IgnoreRelativeEntryInspection::class.java)
    }

    @Throws(Exception::class)
    fun testSimpleCase() {
        doHighlightingFileTest()
    }

    @Throws(Exception::class)
    fun testQuickFix() {
        val name = getTestName(true)
        for (i in 1..5) {
            myFixture.apply {
                configureByIgnoreFile(name + i + FILENAME)
                testHighlighting(true, false, true)
                launchAction(findSingleIntention(IgnoreBundle.message("quick.fix.relative.entry")))
                checkResultByFile("$name$i-after$FILENAME")
            }
        }
    }
}
