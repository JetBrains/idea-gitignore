// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.listeners

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import git4idea.ignore.lang.GitExcludeFileType
import git4idea.ignore.lang.GitIgnoreFileType
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitExcludeLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.lang.kind.MercurialLanguage
import mobi.hsz.idea.gitignore.outer.OuterIgnoreWrapper
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Utils
import org.zmlx.hg4idea.ignore.lang.HgIgnoreFileType
import java.util.ArrayList

/**
 * Component loader for outer ignore files editor.
 */
class OuterIgnoreFileEditorManagerListener : FileEditorManagerListener {

    private val settings = IgnoreSettings.getInstance()

    /**
     * Handles file opening event and attaches outer ignore component.
     *
     * @param source editor manager
     * @param file   current file
     */
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val fileType = file.fileType
        if (!settings.outerIgnoreRules) {
            return
        }
        val language = determineIgnoreLanguage(file, fileType) ?: return

        source.project.let { project ->
            DumbService.getInstance(project).runWhenSmart {
                val outerFiles: MutableList<VirtualFile> = ArrayList(language.getOuterFiles(project, false))
                if (language is GitLanguage) {
                    outerFiles.addAll(GitExcludeLanguage.INSTANCE.getOuterFiles(project))
                    ContainerUtil.removeDuplicates(outerFiles)
                }
                if (outerFiles.isEmpty() || outerFiles.contains(file)) {
                    return@runWhenSmart
                }
                source.getEditors(file).forEach { editor ->
                    if (editor is TextEditor) {
                        val wrapper = OuterIgnoreWrapper(project, language, outerFiles)
                        val component = wrapper.component
                        val settingsListener: IgnoreSettings.Listener = IgnoreSettings.Listener { key, value ->
                            if (IgnoreSettings.KEY.OUTER_IGNORE_RULES == key) {
                                component.isVisible = (value as Boolean?)!!
                            }
                        }
                        settings.addListener(settingsListener)
                        source.addBottomComponent(editor, component)

                        Disposer.register(editor, wrapper)
                        Disposer.register(
                            editor,
                            {
                                settings.removeListener(settingsListener)
                                source.removeBottomComponent(editor, component)
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * If language provided by platform (e.g. GitLanguage) then map to language provided by plugin
     * with extended functionality.
     *
     * @param file     file to check
     * @param fileType file's FileType
     * @return mapped language
     */
    private fun determineIgnoreLanguage(file: VirtualFile, fileType: FileType): IgnoreLanguage? {
        val typeRegistry = FileTypeRegistry.getInstance()
        if (Utils.isGitPluginEnabled) {
            if (typeRegistry.isFileOfType(file, GitIgnoreFileType.INSTANCE)) {
                return GitLanguage.INSTANCE
            }
            if (typeRegistry.isFileOfType(file, GitExcludeFileType.INSTANCE)) {
                return GitExcludeLanguage.INSTANCE
            }
        } else if (Utils.isMercurialPluginEnabled && typeRegistry.isFileOfType(file, HgIgnoreFileType.INSTANCE)) {
            return MercurialLanguage.INSTANCE
        } else if (fileType is IgnoreFileType) {
            return fileType.ignoreLanguage
        }
        return null
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) = Unit

    override fun selectionChanged(event: FileEditorManagerEvent) = Unit
}
