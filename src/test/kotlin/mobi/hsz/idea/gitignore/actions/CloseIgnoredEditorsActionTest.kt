// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.actions

import mobi.hsz.idea.gitignore.Common

class CloseIgnoredEditorsActionTest : Common<Any?>() {

    fun testCloseIgnoredEditorsActionInvocation() {
        val action = CloseIgnoredEditorsAction()

        myFixture.testAction(action).apply {
            assertEquals("Close Ignored", text)
            assertNull(description)
            assertFalse("Action is not visible if there is no Ignore file context", isEnabledAndVisible)
        }
    }
}
