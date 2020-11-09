// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage

/**
 * Entry manipulator.
 */
class IgnoreEntryManipulator : AbstractElementManipulator<IgnoreEntry>() {

    /**
     * Changes the element's text to a new value
     *
     * @param entry      element to be changed
     * @param range      range within the element
     * @param newContent new element text
     * @return changed element
     *
     * @throws IncorrectOperationException if something goes wrong
     */
    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(entry: IgnoreEntry, range: TextRange, newContent: String): IgnoreEntry {
        if (entry.language !is IgnoreLanguage) {
            return entry
        }
        val language = entry.language as IgnoreLanguage
        val fileType = (language.associatedFileType as IgnoreFileType?)!!
        val file = PsiFileFactory.getInstance(entry.project)
            .createFileFromText(language.filename, fileType, range.replace(entry.text, newContent))

        return when (val newEntry = PsiTreeUtil.findChildOfType(file, IgnoreEntry::class.java)) {
            null -> entry
            else -> entry.replace(newEntry) as IgnoreEntry
        }
    }

    /**
     * Returns range of the entry. Skips negation element.
     *
     * @param element element to be changed
     * @return range
     */
    override fun getRangeInElement(element: IgnoreEntry) = element.negation?.run {
        TextRange.create(startOffsetInParent + textLength, element.textLength)
    } ?: super.getRangeInElement(element)
}
