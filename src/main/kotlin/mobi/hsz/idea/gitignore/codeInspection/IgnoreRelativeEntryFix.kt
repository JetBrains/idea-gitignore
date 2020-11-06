// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import java.net.URI
import java.net.URISyntaxException

/**
 * QuickFix action that removes relative parts of the entry [IgnoreRelativeEntryInspection].
 */
class IgnoreRelativeEntryFix(entry: IgnoreEntry) : LocalQuickFixOnPsiElement(entry) {

    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    override fun getText(): String = IgnoreBundle.message("quick.fix.relative.entry")

    /**
     * Handles QuickFix action invoked on [IgnoreEntry].
     *
     * @param project      the [Project] containing the working file
     * @param psiFile      the [PsiFile] containing handled entry
     * @param startElement the [IgnoreEntry] that will be removed
     * @param endElement   the [PsiElement] which is ignored in invoked action
     */
    override fun invoke(project: Project, psiFile: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreEntry) {
            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
            if (document != null) {
                val start = startElement.getStartOffsetInParent()
                val text = startElement.getText()
                val fixed = getFixedPath(text)
                document.replaceString(start, start + text.length, fixed)
            }
        }
    }

    /**
     * Removes relative parts from the given path.
     *
     * @param path element
     * @return fixed path
     */
    private fun getFixedPath(path: String) = path
        .run { replace("/".toRegex(), "/").replace("\\\\\\.".toRegex(), ".") }
        .run {
            try {
                URI(path).normalize().path
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                this
            }
        }
        .run { replace("/\\.{1,2}/".toRegex(), "/").replace("^\\.{0,2}/".toRegex(), "") }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
