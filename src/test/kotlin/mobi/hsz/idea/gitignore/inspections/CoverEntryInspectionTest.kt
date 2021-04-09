// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.inspections

import mobi.hsz.idea.gitignore.codeInspection.IgnoreCoverEntryInspection

class CoverEntryInspectionTest : InspectionTestCase() {

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        myFixture.enableInspections(IgnoreCoverEntryInspection::class.java)
    }

    @Throws(Exception::class)
    fun testEmptyEntries() {
        doHighlightingTest()
    }

    @Throws(Exception::class)
    fun testDuplicates() {
        doHighlightingTest()
    }

    @Throws(Exception::class)
    fun testCovering() {
        doHighlightingTest()
    }
}
