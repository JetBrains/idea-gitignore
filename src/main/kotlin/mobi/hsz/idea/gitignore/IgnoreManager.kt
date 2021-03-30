// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.ProjectTopics
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.ExactFileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.DumbService.DumbModeListener
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsListener
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.Time
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.messages.Topic
import com.jetbrains.rd.util.concurrentMapOf
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.indexing.IgnoreEntryOccurrence
import mobi.hsz.idea.gitignore.indexing.IgnoreFilesIndex
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.CachedConcurrentMap
import mobi.hsz.idea.gitignore.util.Debounced
import mobi.hsz.idea.gitignore.util.ExpiringMap
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.Utils

/**
 * [IgnoreManager] handles ignore files indexing and status caching.
 */
@Suppress("MagicNumber")
class IgnoreManager(private val project: Project) : DumbAware, Disposable {

    private val matcher = project.service<IgnoreMatcher>()
    private val settings = IgnoreSettings.getInstance()
    private val projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project)
    private val changeListManager = project.service<ChangeListManager>()

    private val debouncedStatusesChanged = object : Debounced<Any?>(1000) {
        override fun task(argument: Any?) {
            expiringStatusCache.clear()
            FileStatusManager.getInstance(project).fileStatusesChanged()
        }
    }.also {
        Disposer.register(this, it)
    }

    private val commonRunnableListeners = CommonRunnableListeners(debouncedStatusesChanged)
    private var messageBus = project.messageBus.connect(this)
    private val cachedIgnoreFilesIndex =
        CachedConcurrentMap.create<IgnoreFileType, List<IgnoreEntryOccurrence>> { key -> IgnoreFilesIndex.getEntries(project, key) }

    private val expiringStatusCache = ExpiringMap<VirtualFile, Boolean>(Time.SECOND)

    private val debouncedExitDumbMode = object : Debounced<Boolean?>(3000) {
        override fun task(argument: Boolean?) {
            cachedIgnoreFilesIndex.clear()
            for ((key, value) in FILE_TYPES_ASSOCIATION_QUEUE) {
                associateFileType(key, value)
            }
            debouncedStatusesChanged.run()
        }
    }

    private var working = false
    private val vcsRoots = mutableListOf<VcsRoot>()

    /**
     * Checks if ignored files watching is enabled.
     *
     * @return enabled
     */
    private val isEnabled
        get() = settings.ignoredFileStatus

    /** [VirtualFileListener] instance to check if file's content was changed. */
    private val bulkFileListener = object : BulkFileListener {
        override fun before(events: MutableList<out VFileEvent>) {
            events.forEach {
                handleEvent(it)
            }
        }

        private fun handleEvent(event: VFileEvent) {
            val fileType = event.file?.fileType
            if (fileType is IgnoreFileType) {
                cachedIgnoreFilesIndex.remove(fileType)
                expiringStatusCache.clear()
                debouncedStatusesChanged.run()
            }
        }
    }

    /** [IgnoreSettings] listener to watch changes in the plugin's settings. */
    private val settingsListener = IgnoreSettings.Listener { key, value ->
        when (key) {
            IgnoreSettings.KEY.IGNORED_FILE_STATUS -> toggle(value as Boolean)
            IgnoreSettings.KEY.HIDE_IGNORED_FILES -> ProjectView.getInstance(project).refresh()
            else -> {}
        }
    }

    init {
        toggle(isEnabled)
    }

    /**
     * Checks if file is ignored.
     *
     * @param file current file
     * @return file is ignored
     */
    @Suppress("ComplexCondition", "ComplexMethod", "NestedBlockDepth", "ReturnCount")
    fun isFileIgnored(file: VirtualFile): Boolean {
        expiringStatusCache[file]?.let {
            return it
        }
        if (ApplicationManager.getApplication().isDisposed || project.isDisposed ||
            DumbService.isDumb(project) || !isEnabled || !Utils.isInProject(file, project) ||
            NoAccessDuringPsiEvents.isInsideEventProcessing()
        ) {
            return false
        }
        var ignored = changeListManager.isIgnoredFile(file)
        var matched = false
        var valuesCount = 0
        for (fileType in FILE_TYPES) {
            ProgressManager.checkCanceled()
            if (IgnoreBundle.ENABLED_LANGUAGES[fileType] != true) {
                continue
            }
            val values = cachedIgnoreFilesIndex[fileType] ?: emptyList()
            valuesCount += values.size

            @Suppress("LoopWithTooManyJumpStatements")
            for (value in values) {
                ProgressManager.checkCanceled()
                val entryFile = value.file
                var relativePath = if (entryFile == null) {
                    continue
                } else {
                    Utils.getRelativePath(entryFile.parent, file)
                } ?: continue

                relativePath = StringUtil.trimEnd(StringUtil.trimStart(relativePath, "/"), "/")
                if (StringUtil.isEmpty(relativePath)) {
                    continue
                }
                if (file.isDirectory) {
                    relativePath += "/"
                }
                value.items.forEach {
                    val pattern = Glob.getPattern(it.first!!)
                    if (matcher.match(pattern, relativePath)) {
                        ignored = !it.second
                        matched = true
                    }
                }
            }
        }
        if (valuesCount > 0 && !ignored && !matched) {
            file.parent.let { directory ->
                vcsRoots.forEach { vcsRoot ->
                    ProgressManager.checkCanceled()
                    if (directory == vcsRoot.path) {
                        return expiringStatusCache.set(file, false)
                    }
                }
                return expiringStatusCache.set(file, isFileIgnored(directory))
            }
        }
        return expiringStatusCache.set(file, ignored)
    }

    /** Enable manager. */
    private fun enable() {
        if (working) {
            return
        }
        settings.addListener(settingsListener)

        messageBus.subscribe(
            VirtualFileManager.VFS_CHANGES,
            bulkFileListener
        )
        messageBus.subscribe(
            ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED,
            VcsListener {
                vcsRoots.clear()
                vcsRoots.addAll(projectLevelVcsManager.allVcsRoots)
            }
        )
        messageBus.subscribe(
            DumbService.DUMB_MODE,
            object : DumbModeListener {
                override fun enteredDumbMode() = Unit
                override fun exitDumbMode() {
                    debouncedExitDumbMode.run()
                }
            }
        )
        messageBus.subscribe(ProjectTopics.PROJECT_ROOTS, commonRunnableListeners)
        messageBus.subscribe(RefreshStatusesListener.REFRESH_STATUSES, commonRunnableListeners)
        messageBus.subscribe(ProjectTopics.MODULES, commonRunnableListeners)
        working = true
    }

    /** Disable manager. */
    private fun disable() {
        settings.removeListener(settingsListener)
        working = false
    }

    override fun dispose() {
        disable()
        cachedIgnoreFilesIndex.clear()
    }

    /**
     * Runs [.enable] or [.disable] depending on the passed value.
     *
     * @param enable or disable
     */
    private fun toggle(enable: Boolean) {
        if (enable) {
            enable()
        } else {
            disable()
        }
    }

    fun interface RefreshStatusesListener {

        fun refresh()

        companion object {
            /** Topic to refresh files statuses using [MessageBusConnection]. */
            val REFRESH_STATUSES = Topic("Refresh files statuses", RefreshStatusesListener::class.java)
        }
    }

    companion object {
        /** List of all available [IgnoreFileType]. */
        private val FILE_TYPES = IgnoreBundle.LANGUAGES.map(IgnoreLanguage::fileType)

        /** List of filenames that require to be associated with specific [IgnoreFileType]. */
        val FILE_TYPES_ASSOCIATION_QUEUE = concurrentMapOf<String, IgnoreFileType>()

        /**
         * Associates given file with proper [IgnoreFileType].
         *
         * @param fileName to associate
         * @param fileType file type to bind with pattern
         */
        fun associateFileType(fileName: String, fileType: IgnoreFileType) {
            val application = ApplicationManager.getApplication()
            if (application.isDispatchThread) {
                val fileTypeManager = FileTypeManager.getInstance()
                application.invokeLater(
                    {
                        application.runWriteAction {
                            fileTypeManager.associate(fileType, ExactFileNameMatcher(fileName))
                            FILE_TYPES_ASSOCIATION_QUEUE.remove(fileName)
                        }
                    },
                    ModalityState.NON_MODAL
                )
            } else if (!FILE_TYPES_ASSOCIATION_QUEUE.containsKey(fileName)) {
                FILE_TYPES_ASSOCIATION_QUEUE[fileName] = fileType
            }
        }
    }
}
