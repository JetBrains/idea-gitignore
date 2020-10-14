package mobi.hsz.idea.gitignore.inspections

import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import java.io.File

abstract class InspectionTestCase : BasePlatformTestCase() {

    companion object {
        private val FILENAME = IgnoreLanguage.INSTANCE.filename
    }

    override fun getTestDataPath(): String {
        val url = Thread.currentThread().contextClassLoader.getResource("inspections") ?: return ""
        return File("${url.path}/${name()}").absolutePath
    }

    private fun name() = StringUtil.decapitalize(
        StringUtil.trimEnd(
            StringUtil.trimStart(javaClass.simpleName, "Gitignore"),
            "InspectionTest"
        )
    )

    override fun isWriteActionRequired() = false

    protected fun doHighlightingTest() {
        myFixture.apply {
            copyDirectoryToProject(getTestName(true), getTestName(true))
            testHighlighting(true, false, true, getTestName(true) + "/" + FILENAME)
        }
    }

    protected fun doHighlightingFileTest() {
        myFixture.apply {
            configureByFile(getTestName(true) + FILENAME)
            testHighlighting(true, false, true)
        }
    }

    protected fun doHighlightingFileTestWithQuickFix(quickFixName: String) {
        myFixture.apply {
            configureByFile(getTestName(true) + FILENAME)
            testHighlighting(true, false, true)
            launchAction(findSingleIntention(quickFixName))
            checkResultByFile("${getTestName(true)}-after$FILENAME")
        }
    }
}
