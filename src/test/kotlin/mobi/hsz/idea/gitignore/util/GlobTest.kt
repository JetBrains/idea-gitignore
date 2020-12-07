// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.components.service
import com.intellij.testFramework.UsefulTestCase
import junit.framework.TestCase
import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.InvocationTargetException

@Suppress("UnsafeCallOnNullableType")
class GlobTest : Common<Glob>() {

    @Test
    @Throws(
        InvocationTargetException::class,
        NoSuchMethodException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    fun testPrivateConstructor() {
        privateConstructor(Glob::class.java)
    }

    @Test
    fun testFind() {
        myFixture.apply {
            configureByText(
                IgnoreFileType.INSTANCE,
                createIgnoreContent("foo.txt", "bar.txt", "buz.txt", "vcsdir", "dir")
            )
            addFileToProject("bar.txt", "bar content")
            addFileToProject("buz.txt", "buz content")
            addFileToProject("vcsdir/.git/info", "vcsdir git info content")
            addFileToProject("dir/buz.txt", "buz2 content")
            addFileToProject("dir/biz.txt", "buz2 content")
        }
        val dir = fixtureRootFile.findChild("dir")
        TestCase.assertNotNull(dir)

        val matcher = project.service<IgnoreMatcher>()
        var result = Glob.find(fixtureRootFile, fixtureChildrenEntries, matcher, false)

        // foo.txt
        result[fixtureChildrenEntries[0]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertEmpty(it)
        }

        // bar.txt
        result[fixtureChildrenEntries[1]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 1)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("bar.txt")))
        }

        // buz.txt
        result[fixtureChildrenEntries[2]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 2)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("buz.txt")))
            TestCase.assertTrue(it.contains(dir!!.findChild("buz.txt")))
        }

        // ignore VCS directory
        result[fixtureChildrenEntries[3]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 1)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("vcsdir")))
        }

        // dir
        result[fixtureChildrenEntries[4]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 1)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("dir")))

            // dir not includeNested
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 1)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("dir")))
        }

        // dir includeNested
        result = Glob.find(fixtureRootFile, fixtureChildrenEntries, matcher, true)
        result[fixtureChildrenEntries[4]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 3)
            TestCase.assertTrue(it.contains(fixtureRootFile.findChild("dir")))
            TestCase.assertTrue(it.contains(dir?.findChild("buz.txt")))
            TestCase.assertTrue(it.contains(dir?.findChild("biz.txt")))
        }
    }

    @Test
    fun testFindAsPaths() {
        myFixture.apply {
            configureByText(
                IgnoreFileType.INSTANCE,
                createIgnoreContent("foo.txt", "bar.txt", "buz.txt", "vcsdir", "dir")
            )
            addFileToProject("bar.txt", "bar content")
            addFileToProject("buz.txt", "buz content")
            addFileToProject("vcsdir/.git/info", "vcsdir git info content")
            addFileToProject("dir/buz.txt", "buz2 content")
            addFileToProject("dir/biz.txt", "buz2 content")
        }
        val dir = fixtureRootFile.findChild("dir")
        TestCase.assertNotNull(dir)

        val matcher = project.service<IgnoreMatcher>()
        val result = Glob.findAsPaths(fixtureRootFile, fixtureChildrenEntries, matcher, false)

        // foo.txt
        result[fixtureChildrenEntries[0]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertEmpty(it)
        }

        // bar.txt
        result[fixtureChildrenEntries[1]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 1)
            TestCase.assertTrue(it.contains("bar.txt"))
        }

        // buz.txt
        result[fixtureChildrenEntries[2]]!!.let {
            TestCase.assertNotNull(result)
            UsefulTestCase.assertNotEmpty(it)
            TestCase.assertEquals(it.size, 2)
            TestCase.assertTrue(it.contains("buz.txt"))
            TestCase.assertTrue(it.contains("dir/buz.txt"))
        }
    }

    @Test
    @Throws(Exception::class)
    @Suppress("LongMethod")
    fun testCreatePattern() {
        Glob.createPattern("file.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/subdir/file.txt").matches())
            Assert.assertFalse(it.matcher("file1.txt").matches())
            Assert.assertFalse(it.matcher("otherfile.txt").matches())
        }

        Glob.createPattern("file*.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/file.txt").matches())
        }

        Glob.createPattern("fil[eE].txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("file.txt").matches())
            TestCase.assertTrue(it.matcher("filE.txt").matches())
            Assert.assertFalse(it.matcher("fild.txt").matches())
        }

        Glob.createPattern("dir/file.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("dir/file.txt").matches())
            Assert.assertFalse(it.matcher("xdir/dir/file.txt").matches())
            Assert.assertFalse(it.matcher("xdir/file.txt").matches())
        }

        Glob.createPattern("/file.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("file.txt").matches())
            Assert.assertFalse(it.matcher("dir/file.txt").matches())
        }

        Glob.createPattern("fi**le.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("file.txt").matches())
            TestCase.assertTrue(it.matcher("fi-foo-le.txt").matches())
            Assert.assertFalse(it.matcher("fi/le.txt").matches())
            Assert.assertFalse(it.matcher("fi/foo/le.txt").matches())
        }

        Glob.createPattern("**/dir/file.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("foo/dir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/file.txt").matches())
        }

        Glob.createPattern("/dir/**/file.txt", IgnoreBundle.Syntax.GLOB).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("dir/subdir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/subdir/foo/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/file.txt").matches())
        }

        Glob.createPattern("dir/*", IgnoreBundle.Syntax.GLOB, true).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("dir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/subdir/").matches())
            Assert.assertFalse(it.matcher("dir/").matches())
        }

        Glob.createPattern("subdir", IgnoreBundle.Syntax.GLOB, true).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("dir/subdir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/subdir/").matches())
            Assert.assertFalse(it.matcher("dir/foo/bar.txt").matches())
        }

        Glob.createPattern("subdir/", IgnoreBundle.Syntax.GLOB, true).let {
            TestCase.assertNotNull(it)
            TestCase.assertTrue(it!!.matcher("dir/subdir/file.txt").matches())
            TestCase.assertTrue(it.matcher("dir/subdir/").matches())
            Assert.assertFalse(it.matcher("dir/foo/bar.txt").matches())
        }
    }
}
