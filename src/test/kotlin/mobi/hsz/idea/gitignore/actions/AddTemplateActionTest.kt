// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.actions

import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

class AddTemplateActionTest : Common<AddTemplateAction>() {

    fun testAddTemplateActionInvocation() {
        val action = AddTemplateAction()

        myFixture.testAction(action).apply {
            assertEquals(IgnoreBundle.message("action.addTemplate"), text)
            assertEquals(IgnoreBundle.message("action.addTemplate.description"), description)
            assertFalse("Action is not visible if there is no Ignore file context", isEnabledAndVisible)
            myFixture.configureByText(IgnoreFileType.INSTANCE, "foo")
        }

//        myFixture.testAction(action).apply {
//            assertTrue("Action is visible if there is Ignore file context", isEnabledAndVisible)
//        }
    }
}
