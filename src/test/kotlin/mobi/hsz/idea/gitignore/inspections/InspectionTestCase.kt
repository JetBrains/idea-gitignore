package mobi.hsz.idea.gitignore.inspections

import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ResourceUtil
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import java.io.File

@Suppress("UnnecessaryAbstractClass")
abstract class InspectionTestCase : BasePlatformTestCase() {

    companion object {
        val FILENAME = IgnoreLanguage.INSTANCE.filename
    }

    override fun getTestDataPath(): String {
        val url = Thread.currentThread().contextClassLoader.getResource("inspections") ?: return ""
        return File(url.path + "/" + name()).absolutePath
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
            configureByIgnoreFile(getTestName(true) + FILENAME)
            testHighlighting(true, false, true)
        }
    }

    protected fun doHighlightingFileTestWithQuickFix(quickFixName: String) {
        myFixture.apply {
            configureByIgnoreFile(getTestName(true) + FILENAME)
            testHighlighting(true, false, true)
            launchAction(findSingleIntention(quickFixName))
            checkResultByFile("${getTestName(true)}-after$FILENAME")
        }
    }

    protected fun configureByIgnoreFile(fileName: String) {
        val resource = javaClass.classLoader.getResourceAsStream("inspections/${name()}/$fileName") ?: return
        val text = ResourceUtil.loadText(resource)

        myFixture.configureByText(IgnoreFileType.INSTANCE, text)
    }
}
