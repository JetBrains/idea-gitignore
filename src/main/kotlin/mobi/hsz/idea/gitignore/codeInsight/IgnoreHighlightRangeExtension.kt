// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInsight

import com.intellij.codeInsight.daemon.impl.HighlightRangeExtension
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage

/**
 * Highlight common ranges in the file.
 */
class IgnoreHighlightRangeExtension : HighlightRangeExtension {

    /**
     * Checks if current [PsiFile] is allowed to enable range highlighting.
     *
     * @param file current file
     * @return allowed to highlight
     */
    override fun isForceHighlightParents(file: PsiFile) = file.language is IgnoreLanguage
}
