// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore

import com.intellij.openapi.util.text.StringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.util.Constants
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier

@Suppress("UnnecessaryAbstractClass")
abstract class Common<T> : BasePlatformTestCase() {

    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    protected fun privateConstructor(clz: Class<T>) {
        val constructor = clz.getDeclaredConstructor()
        TestCase.assertTrue(Modifier.isPrivate(constructor.modifiers))
        constructor.isAccessible = true
        constructor.newInstance()
    }

    protected fun createIgnoreContent(vararg entries: String?) = StringUtil.join(entries, Constants.NEWLINE)

    protected val fixtureRootFile
        get() = myFixture.file.containingDirectory.virtualFile

    protected val fixtureChildrenEntries: List<IgnoreEntry>
        get() {
            val children: MutableList<IgnoreEntry> = mutableListOf()
            myFixture.file.acceptChildren(
                object : IgnoreVisitor() {
                    override fun visitEntry(entry: IgnoreEntry) {
                        children.add(entry)
                    }
                }
            )
            return children
        }
}
