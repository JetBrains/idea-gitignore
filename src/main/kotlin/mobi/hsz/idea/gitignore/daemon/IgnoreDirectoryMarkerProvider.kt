// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.daemon

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.PlatformIcons
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.psi.IgnoreEntryDirectory
import mobi.hsz.idea.gitignore.psi.IgnoreEntryFile
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.Utils

/**
 * [LineMarkerProvider] that marks entry lines with directory icon if they point to the directory in virtual system.
 */
class IgnoreDirectoryMarkerProvider : LineMarkerProvider {

    /** Cache map. */
    private val cache = mutableMapOf<String, Boolean>()

    /**
     * Returns [LineMarkerInfo] with set [PlatformIcons.FOLDER_ICON] if entry points to the directory.
     *
     * @param element current element
     * @return `null` if entry is not a directory
     */
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is IgnoreEntryFile) {
            return null
        }
        var isDirectory = element is IgnoreEntryDirectory

        if (!isDirectory) {
            val key = element.getText()
            if (cache.containsKey(key)) {
                isDirectory = cache[key]!!
            } else {
                val parent = element.getContainingFile().virtualFile.parent ?: return null
                val project = element.getProject()
                Utils.getModuleForFile(parent, project) ?: return null

                val matcher = IgnoreManager.getInstance(project).matcher
                val file = Glob.findOne(parent, element, matcher)
                cache[key] = file != null && file.isDirectory.also { isDirectory = it }
            }
        }

        return if (isDirectory) {
            LineMarkerInfo(
                element.getFirstChild(),
                element.getTextRange(),
                PlatformIcons.FOLDER_ICON,
                null,
                null,
                GutterIconRenderer.Alignment.CENTER
            )
        } else null
    }
}
