// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.actions

import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.IgnoreBundle

class IgnoreFileActionTest : Common<IgnoreFileAction?>() {

    fun testAddTemplateActionInvocation() {
        val action = IgnoreFileAction()
        
        myFixture.testAction(action).apply {
            assertEquals(IgnoreBundle.message("action.addToIgnore", "null"), text)
            assertEquals(IgnoreBundle.message("action.addToIgnore.description", "null"), description)
            assertFalse("Action is not visible if there is no Ignore file context", isEnabledAndVisible)
        }
    }
}
