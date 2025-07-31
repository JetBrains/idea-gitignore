// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.vfs.VirtualFile

/**
 * Definition of [com.intellij.lexer.FlexAdapter].
 */
class IgnoreLexerAdapter(virtualFile: VirtualFile? = null) : FlexAdapter(IgnoreLexer(virtualFile))
