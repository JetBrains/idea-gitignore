// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.completion

import com.intellij.openapi.command.WriteCommandAction.writeCommandAction
import com.intellij.testFramework.UsefulTestCase
import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import java.io.IOException

class CompletionTest : Common<Any?>() {

    fun testSimple() {
        myFixture.tempDirFixture.createFile("fileName.txt")
        doTest("fileN<caret>", "fileName.txt<caret>")
    }

    @Throws(IOException::class)
    fun testCurrentDirectoryAlias() {
        writeCommandAction(project).run<Throwable> {
            myFixture.tempDirFixture.createFile("fileName.txt")
        }
        doTest("./fileN<caret>", "./fileName.txt<caret>")
    }

    @Throws(IOException::class)
    fun testInHiddenDirectory() {
        writeCommandAction(project).run<Throwable> {
            myFixture.tempDirFixture.findOrCreateDir(".hidden").createChildData(this, "fileName.txt")
        }
        doTest(".hidden/fileN<caret>", ".hidden/fileName.txt<caret>")
    }

    @Throws(IOException::class)
    fun testInGlobDirectory() {
        writeCommandAction(project).run<Throwable> {
            myFixture.tempDirFixture.findOrCreateDir("glob1").createChildData(this, "fileName1.txt")
            myFixture.tempDirFixture.findOrCreateDir("glob2").createChildData(this, "fileName2.txt")
        }
        doTestVariants("*/fileN<caret>", "fileName1.txt", "fileName2.txt")
    }

    @Throws(IOException::class)
    fun testNegation() {
        myFixture.tempDirFixture.createFile("fileName.txt")
        doTest("!fileNa<caret>", "!fileName.txt<caret>")
    }

    private fun doTest(beforeText: String, afterText: String) {
        myFixture.configureByText(IgnoreFileType.INSTANCE, beforeText)
        myFixture.completeBasic()
        myFixture.checkResult(afterText)
    }

    private fun doTestVariants(beforeText: String, vararg variants: String) {
        myFixture.configureByText(IgnoreFileType.INSTANCE, beforeText)
        myFixture.completeBasic()
        UsefulTestCase.assertContainsElements(myFixture.lookupElementStrings!!.toList(), *variants)
    }
}
