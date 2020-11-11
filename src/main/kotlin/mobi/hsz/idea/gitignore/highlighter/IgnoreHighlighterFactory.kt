// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.highlighter

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * [SyntaxHighlighterFactory] class definition.
 */
class IgnoreHighlighterFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?) = IgnoreHighlighter(virtualFile)
}
