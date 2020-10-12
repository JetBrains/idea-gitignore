// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.actions

import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.IgnoreBundle

class HideIgnoredFilesActionTest : Common<AddTemplateAction>() {

    fun testHideIgnoredFilesActionInvocation() {
        val action = HideIgnoredFilesAction()

        myFixture.testAction(action).apply {
            assertEquals(IgnoreBundle.message("action.hideIgnoredVisibility"), text)
        }
    }
}
