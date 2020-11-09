// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.outer

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.messages.MessageBusConnection
import git4idea.ignore.lang.GitExcludeFileType
import git4idea.ignore.lang.GitIgnoreFileType
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitExcludeLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.lang.kind.MercurialLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.settings.IgnoreSettings.Companion.getInstance
import mobi.hsz.idea.gitignore.util.Utils
import org.jetbrains.annotations.NonNls
import org.zmlx.hg4idea.ignore.lang.HgIgnoreFileType
import java.util.ArrayList

/**
 * Component loader for outer ignore files editor.
 */
class OuterIgnoreLoaderComponent(private val project: Project) : ProjectComponent {
    /** MessageBus instance.  */
    private var messageBus: MessageBusConnection? = null

    companion object {
        fun getInstance(project: Project): OuterIgnoreLoaderComponent = project.getComponent(OuterIgnoreLoaderComponent::class.java)
    }

    @NonNls
    override fun getComponentName() = "IgnoreOuterComponent"

    /** Initializes component.  */
    override fun initComponent() {
        messageBus = project.messageBus.connect()
        messageBus!!.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, IgnoreEditorManagerListener(project))
    }

    override fun disposeComponent() {
        if (messageBus != null) {
            messageBus!!.disconnect()
            messageBus = null
        }
    }

    /** Listener for ignore editor manager.  */
    private class IgnoreEditorManagerListener
    /** Constructor.  */(
        /** Current project.  */
        private val project: Project
    ) : FileEditorManagerListener {

        /**
         * Handles file opening event and attaches outer ignore component.
         *
         * @param source editor manager
         * @param file   current file
         */
        override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
            val fileType = file.fileType
            if (!getInstance().outerIgnoreRules) {
                return
            }
            val language = determineIgnoreLanguage(file, fileType) ?: return
            DumbService.getInstance(project).runWhenSmart {
                val outerFiles: MutableList<VirtualFile> = ArrayList(language.getOuterFiles(project, false))
                if (language is GitLanguage) {
                    outerFiles.addAll(GitExcludeLanguage.INSTANCE.getOuterFiles(project))
                    ContainerUtil.removeDuplicates(outerFiles)
                }
                if (outerFiles.isEmpty() || outerFiles.contains(file)) {
                    return@runWhenSmart
                }
                for (fileEditor in source.getEditors(file)) {
                    if (fileEditor is TextEditor) {
                        val wrapper = OuterIgnoreWrapper(project, language, outerFiles)
                        val component = wrapper.component
                        val settingsListener: IgnoreSettings.Listener = IgnoreSettings.Listener { key, value ->
                            if (IgnoreSettings.KEY.OUTER_IGNORE_RULES == key) {
                                component.isVisible = (value as Boolean?)!!
                            }
                        }
                        getInstance().addListener(settingsListener)
                        source.addBottomComponent(fileEditor, component)
                        Disposer.register(fileEditor, wrapper)
                        Disposer.register(
                            fileEditor,
                            {
                                getInstance().removeListener(settingsListener)
                                source.removeBottomComponent(fileEditor, component)
                            }
                        )
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

    /** Outer file fetcher event interface. */
    fun interface OuterFileFetcher {

        fun fetch(project: Project): Collection<VirtualFile?>
    }
}
