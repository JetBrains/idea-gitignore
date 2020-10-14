// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.refactoring

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import java.io.IOException

class RenameTest : BasePlatformTestCase() {

    override fun isWriteActionRequired() = true

    @Throws(IOException::class)
    fun testRenameFile() {
        myFixture.apply {
            tempDirFixture.findOrCreateDir("dir").createChildData(this, "file.txt")
        }
        doTest("*/fil<caret>e.txt", "newFile.txt", "dir/newFile.txt")
    }

    @Throws(IOException::class)
    fun testRenameDirectory() {
        myFixture.apply {
            tempDirFixture.findOrCreateDir("dir").createChildData(this, "file.txt")
        }
        doTest("di<caret>r/file.txt", "newDir", "newDir/file.txt")
    }

    @Throws(IOException::class)
    fun testRenameInNegationEntry() {
        myFixture.apply {
            tempDirFixture.findOrCreateDir("dir").createChildData(this, "file.txt")
        }
        doTest("!di<caret>r/file.txt", "newDir", "!newDir/file.txt")
    }

    private fun doTest(beforeText: String, newName: String, afterText: String) {
        myFixture.apply {
            configureByText(IgnoreFileType.INSTANCE, beforeText)
            renameElementAtCaret(newName)
            checkResult(afterText)
        }
    }
}
