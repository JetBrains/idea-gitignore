// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.command

import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Notify
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Command action that appends specified file to rules list.
 */
class AppendFileCommandAction(
    private val project: Project,
    private val file: PsiFile,
    private val content: MutableSet<String>,
    private val ignoreDuplicates: Boolean = false,
    private val ignoreComments: Boolean = false,
) : CommandAction<PsiFile?>(project) {

    private val settings = service<IgnoreSettings>()

    /**
     * Adds [.content] to the given [.file]. Checks if file contains content and sends a notification.
     *
     * @return previously provided file
     */
    @Suppress("ComplexMethod", "LongMethod", "NestedBlockDepth")
    override fun compute(): PsiFile {
        if (content.isEmpty()) {
            return file
        }
        val manager = PsiDocumentManager.getInstance(project)
        manager.getDocument(file)?.let { document ->
            var offset = document.textLength

            file.acceptChildren(
                object : IgnoreVisitor() {
                    override fun visitEntry(entry: IgnoreEntry) {
                        val moduleDir = Utils.getModuleRootForFile(file.virtualFile, project)
                        if (content.contains(entry.text) && moduleDir != null) {
                            Notify.show(
                                project,
                                IgnoreBundle.message("action.appendFile.entryExists", entry.text),
                                IgnoreBundle.message(
                                    "action.appendFile.entryExists.in",
                                    Utils.getRelativePath(moduleDir, file.virtualFile)
                                ),
                                NotificationType.WARNING
                            )
                            content.remove(entry.text)
                        }
                    }
                }
            )

            if (settings.insertAtCursor) {
                EditorFactory.getInstance().getEditors(document).firstOrNull()?.let { editor ->
                    editor.selectionModel.selectionStartPosition?.let { position ->
                        offset = document.getLineStartOffset(position.line)
                    }
                }
            }

            content.forEach { it ->
                var entry = it

                if (ignoreDuplicates) {
                    val currentLines = document.text.split(Constants.NEWLINE).filter {
                        it.isNotEmpty() && !it.startsWith(Constants.HASH)
                    }.toMutableList()
                    val entryLines = it.split(Constants.NEWLINE).toMutableList()
                    val iterator = entryLines.iterator()

                    while (iterator.hasNext()) {
                        val line = iterator.next().trim { it <= ' ' }
                        if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                            continue
                        }
                        if (currentLines.contains(line)) {
                            iterator.remove()
                        } else {
                            currentLines.add(line)
                        }
                    }
                    entry = StringUtil.join(entryLines, Constants.NEWLINE)
                }
                if (ignoreComments) {
                    val entryLines = it.split(Constants.NEWLINE).toMutableList()
                    val iterator = entryLines.iterator()
                    while (iterator.hasNext()) {
                        val line = iterator.next().trim { it <= ' ' }
                        if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                            iterator.remove()
                        }
                    }
                    entry = StringUtil.join(entryLines, Constants.NEWLINE)
                }

                entry = StringUtil.replace(entry, "\r", "")
                if (!StringUtil.isEmpty(entry)) {
                    entry += Constants.NEWLINE
                }

                if (!settings.insertAtCursor && !document.text.endsWith(Constants.NEWLINE) && entry.isNotEmpty()) {
                    entry = Constants.NEWLINE + entry
                }

                document.insertString(offset, entry)
                offset += entry.length
            }

            manager.commitDocument(document)
        }
        return file
    }
}
