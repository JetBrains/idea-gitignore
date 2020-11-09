// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.util

import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.util.exec.ExternalExec
import org.junit.Test
import java.lang.reflect.InvocationTargetException

class ExternalExecTest : Common<ExternalExec>() {

    @Test
    @Throws(
        InvocationTargetException::class,
        NoSuchMethodException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    fun testPrivateConstructor() {
        privateConstructor(ExternalExec::class.java)
    }
}
