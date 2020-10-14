// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.inspections

import mobi.hsz.idea.gitignore.codeInspection.IgnoreUnusedEntryInspection

class UnusedEntryInspectionTest : InspectionTestCase() {

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()
        myFixture.enableInspections(IgnoreUnusedEntryInspection::class.java)
    }

    @Throws(Exception::class)
    fun testUnusedFile() {
        doHighlightingTest()
    }

    @Throws(Exception::class)
    fun testUnusedDirectory() {
        doHighlightingTest()
    }
}
