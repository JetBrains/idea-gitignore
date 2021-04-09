// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.inspections

import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.codeInspection.IgnoreDuplicateEntryInspection

class DuplicateEntryInspectionTest : InspectionTestCase() {

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        myFixture.enableInspections(IgnoreDuplicateEntryInspection::class.java)
    }

    @Throws(Exception::class)
    fun testSimpleCase() {
        doHighlightingFileTest()
    }

    @Throws(Exception::class)
    fun testSimpleCaseWithQuickFix() {
        doHighlightingFileTestWithQuickFix(IgnoreBundle.message("quick.fix.remove.entry"))
    }
}
