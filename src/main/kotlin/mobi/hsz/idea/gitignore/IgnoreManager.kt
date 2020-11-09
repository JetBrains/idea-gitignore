// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.ProjectTopics
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileTypes.ExactFileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.DumbService.DumbModeListener
import com.intellij.openapi.project.NoAccessDuringPsiEvents
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.VcsListener
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileCopyEvent
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileMoveEvent
import com.intellij.util.Time
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.messages.Topic
import git4idea.GitVcs
import mobi.hsz.idea.gitignore.IgnoreManager.RefreshTrackedIgnoredListener
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType.Companion.getWorkingDirectory
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType.Companion.INSTANCE
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor.Companion.getAdditionalFiles
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor.Companion.invalidateCache
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor.Companion.invalidateDisposedProjects
import mobi.hsz.idea.gitignore.indexing.IgnoreEntryOccurrence
import mobi.hsz.idea.gitignore.indexing.IgnoreFilesIndex.Companion.getEntries
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.CachedConcurrentMap
import mobi.hsz.idea.gitignore.util.Debounced
import mobi.hsz.idea.gitignore.util.ExpiringMap
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.InterruptibleScheduledFuture
import mobi.hsz.idea.gitignore.util.MatcherUtil
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.ExternalExec.getIgnoredFiles
import org.jetbrains.annotations.NonNls
import java.util.ArrayList
import java.util.HashSet
import java.util.concurrent.ConcurrentMap

/**
 * [IgnoreManager] handles ignore files indexing and status caching.
 */
class IgnoreManager(private val project: Project) : DumbAware, ProjectComponent {

    /** Returns [MatcherUtil] instance which is required for sharing matcher cache. */
    val matcher = MatcherUtil()

    /** [VirtualFileManager] instance. */
    private val virtualFileManager = VirtualFileManager.getInstance()

    /** [IgnoreSettings] instance. */
    private val settings = IgnoreSettings.getInstance()

    /** [ProjectLevelVcsManager] instance. */
    private val projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project)

    /** [RefreshTrackedIgnoredRunnable] instance. */
    private val refreshTrackedIgnoredRunnable = RefreshTrackedIgnoredRunnable()

    /** [FileStatusManager.fileStatusesChanged] method wrapped with [Debounced]. */
    private val debouncedStatusesChanged = object : Debounced<Any?>(1000) {
        override fun task(argument: Any?) {
            expiringStatusCache.clear()
            FileStatusManager.getInstance(project).fileStatusesChanged()
        }
    }

    /** Common runnable listeners. */
    private val commonRunnableListeners = CommonRunnableListeners(debouncedStatusesChanged)

    /** [MessageBusConnection] instance. */
    private var messageBus: MessageBusConnection? = null

    /** List of the files that are ignored and also tracked by Git. */
    private val confirmedIgnoredFiles = ContainerUtil.createConcurrentWeakMap<VirtualFile, VcsRoot>()

    /** List of the new files that were not covered by [.confirmedIgnoredFiles] yet. */
    private val notConfirmedIgnoredFiles = HashSet<VirtualFile>()

    /** References to the indexed [IgnoreEntryOccurrence]. */
    private val cachedIgnoreFilesIndex =
        CachedConcurrentMap.create<IgnoreFileType, List<IgnoreEntryOccurrence>> { key -> getEntries(project, key) }

    /** References to the indexed outer files. */
    private val cachedOuterFiles =
        CachedConcurrentMap.create<IgnoreFileType, Collection<VirtualFile>> { key -> key.ignoreLanguage.getOuterFiles(project) }

    private val expiringStatusCache = ExpiringMap<VirtualFile, Boolean>(Time.SECOND)

    /** [FileStatusManager.fileStatusesChanged] method wrapped with [Debounced]. */
    private val debouncedRefreshTrackedIgnores = object : Debounced<Boolean>(1000) {
        override fun task(argument: Boolean?) {
            if (argument == true) {
                refreshTrackedIgnoredRunnable.refresh()
            } else {
                refreshTrackedIgnoredRunnable.run()
            }
        }
    }

    /** [DumbService.DumbModeListener.exitDumbMode] method body wrapped with [Debounced]. */
    private val debouncedExitDumbMode = object : Debounced<Boolean?>(3000) {
        override fun task(argument: Boolean?) {
            cachedIgnoreFilesIndex.clear()
            for ((key, value) in FILE_TYPES_ASSOCIATION_QUEUE) {
                associateFileType(key, value)
            }
            debouncedStatusesChanged.run()
        }
    }

    /** Scheduled feature connected with [.debouncedRefreshTrackedIgnores]. */
    private val refreshTrackedIgnoredFeature = InterruptibleScheduledFuture(debouncedRefreshTrackedIgnores, 10000, 5).apply {
        setTrailing(true)
    }

    /** [IgnoreManager] working flag. */
    private var working = false

    /** List of available VCS roots for the current project. */
    private val vcsRoots: MutableList<VcsRoot> = ArrayList()

    /**
     * Checks if ignored files watching is enabled.
     *
     * @return enabled
     */
    private val isEnabled
        get() = settings.ignoredFileStatus

    /** [VirtualFileListener] instance to check if file's content was changed. */
    private val virtualFileListener: VirtualFileListener = object : VirtualFileListener {
        override fun contentsChanged(event: VirtualFileEvent) {
            handleEvent(event)
        }

        override fun fileCreated(event: VirtualFileEvent) {
            handleEvent(event)
            notConfirmedIgnoredFiles.add(event.file)
            debouncedRefreshTrackedIgnores.run(true)
        }

        override fun fileDeleted(event: VirtualFileEvent) {
            handleEvent(event)
            notConfirmedIgnoredFiles.add(event.file)
            debouncedRefreshTrackedIgnores.run(true)
        }

        override fun fileMoved(event: VirtualFileMoveEvent) {
            handleEvent(event)
            notConfirmedIgnoredFiles.add(event.file)
            debouncedRefreshTrackedIgnores.run(true)
        }

        override fun fileCopied(event: VirtualFileCopyEvent) {
            handleEvent(event)
            notConfirmedIgnoredFiles.add(event.file)
            debouncedRefreshTrackedIgnores.run(true)
        }

        private fun handleEvent(event: VirtualFileEvent) {
            val fileType = event.file.fileType
            if (fileType is IgnoreFileType) {
                cachedIgnoreFilesIndex.remove(fileType)
                cachedOuterFiles.remove(fileType)
                if (fileType is GitExcludeFileType) {
                    cachedOuterFiles.remove(INSTANCE)
                }
                expiringStatusCache.clear()
                debouncedStatusesChanged.run()
                debouncedRefreshTrackedIgnores.run()
            }
        }
    }

    /** [IgnoreSettings] listener to watch changes in the plugin's settings. */
    private val settingsListener = IgnoreSettings.Listener { key, value ->
        when (key) {
            IgnoreSettings.KEY.IGNORED_FILE_STATUS -> toggle((value as Boolean?)!!)
            IgnoreSettings.KEY.OUTER_IGNORE_RULES, IgnoreSettings.KEY.LANGUAGES -> {
                IgnoreBundle.ENABLED_LANGUAGES.clear()
                if (isEnabled) {
                    if (working) {
                        debouncedStatusesChanged.run()
                        debouncedRefreshTrackedIgnores.run()
                    } else {
                        enable()
                    }
                }
            }
            IgnoreSettings.KEY.HIDE_IGNORED_FILES -> ProjectView.getInstance(project).refresh()
        }
    }

    /**
     * Checks if file is ignored.
     *
     * @param file current file
     * @return file is ignored
     */
    fun isFileIgnored(file: VirtualFile): Boolean {
        val cached = expiringStatusCache[file]
        if (cached != null) {
            return cached
        }
        if (ApplicationManager.getApplication().isDisposed || project.isDisposed ||
            DumbService.isDumb(project) || !isEnabled || !Utils.isInProject(file, project) ||
            NoAccessDuringPsiEvents.isInsideEventProcessing()
        ) {
            return false
        }
        var ignored = false
        var matched = false
        var valuesCount = 0
        for (fileType in FILE_TYPES) {
            ProgressManager.checkCanceled()
            if (!IgnoreBundle.ENABLED_LANGUAGES[fileType]!!) {
                continue
            }
            val values: Collection<IgnoreEntryOccurrence> = ContainerUtil.notNullize(
                cachedIgnoreFilesIndex[fileType]
            )
            valuesCount += values.size
            for (value in values) {
                ProgressManager.checkCanceled()
                var relativePath: String?
                val entryFile = value.file
                relativePath = if (entryFile == null) {
                    continue
                } else if (fileType is GitExcludeFileType) {
                    val workingDirectory = getWorkingDirectory(
                        project, entryFile
                    )
                    if (workingDirectory == null || !Utils.isUnder(file, workingDirectory)) {
                        continue
                    }
                    Utils.getRelativePath(workingDirectory, file)
                } else {
                    val vcsRoot = getVcsRootFor(file)
                    if (vcsRoot != null && !Utils.isUnder(entryFile, vcsRoot)) {
                        if (!cachedOuterFiles[fileType]!!.contains(entryFile)) {
                            continue
                        }
                    }
                    if (getAdditionalFiles(project).contains(entryFile)) {
                        val projectDir = Utils.guessProjectDir(project)
                        Utils.getRelativePath(projectDir!!, file)
                    } else {
                        Utils.getRelativePath(entryFile.parent, file)
                    }
                }
                if (relativePath == null) {
                    continue
                }
                relativePath = StringUtil.trimEnd(StringUtil.trimStart(relativePath, "/"), "/")
                if (StringUtil.isEmpty(relativePath)) {
                    continue
                }
                if (file.isDirectory) {
                    relativePath += "/"
                }
                for (item in value.items) {
                    val pattern = Glob.getPattern(item.first!!)
                    if (matcher.match(pattern, relativePath)) {
                        ignored = !item.second!!
                        matched = true
                    }
                }
            }
        }
        if (valuesCount > 0 && !ignored && !matched) {
            val directory = file.parent
            if (directory != null) {
                for (vcsRoot in vcsRoots) {
                    ProgressManager.checkCanceled()
                    if (directory == vcsRoot.path) {
                        return expiringStatusCache.set(file, false)
                    }
                }
                return expiringStatusCache.set(file, isFileIgnored(directory))
            }
        }
        if (ignored) {
            refreshTrackedIgnoredFeature.cancel()
        }
        return expiringStatusCache.set(file, ignored)
    }

    /**
     * Finds [VirtualFile] directory of [VcsRoot] that contains passed file.
     *
     * @param file to check
     * @return VCS Root for given file
     */
    private fun getVcsRootFor(file: VirtualFile): VirtualFile? {
        val vcsRoot = ContainerUtil.find(
            ContainerUtil.reverse(vcsRoots)
        ) { root: VcsRoot -> Utils.isUnder(file, root.path) }
        return vcsRoot?.path
    }

    /**
     * Checks if file is ignored and tracked.
     *
     * @param file current file
     * @return file is ignored and tracked
     */
    fun isFileTracked(file: VirtualFile): Boolean {
        return !notConfirmedIgnoredFiles.contains(file) && !confirmedIgnoredFiles.isEmpty() &&
            confirmedIgnoredFiles.containsKey(file)
    }

    /**
     * Invoked when the project corresponding to this component instance is opened.
     *
     * Note that components may be
     * created for even unopened projects and this method can be never invoked for a particular component instance (for
     * example for default project).
     */
    override fun projectOpened() {
        invalidateDisposedProjects()
        if (isEnabled && !working) {
            enable()
        }
    }

    /**
     * Invoked when the project corresponding to this component instance is closed.
     *
     * Note that components may be
     * created for even unopened projects and this method can be never invoked for a particular component instance (for
     * example for default project).
     */
    override fun projectClosed() {
        invalidateDisposedProjects()
        disable()
    }

    /** Enable manager. */
    private fun enable() {
        if (working) {
            return
        }
        refreshTrackedIgnoredFeature.run()
        virtualFileManager.addVirtualFileListener(virtualFileListener)
        settings.addListener(settingsListener)
        messageBus = project.messageBus.connect()
        messageBus!!.subscribe(
            RefreshTrackedIgnoredListener.TRACKED_IGNORED_REFRESH,
            RefreshTrackedIgnoredListener { debouncedRefreshTrackedIgnores.run(true) })
        messageBus!!.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, VcsListener {
            invalidateCache(project)
            vcsRoots.clear()
            vcsRoots.addAll(ContainerUtil.newArrayList(*projectLevelVcsManager.allVcsRoots))
        })
        messageBus!!.subscribe(DumbService.DUMB_MODE, object : DumbModeListener {
            override fun enteredDumbMode() {}
            override fun exitDumbMode() {
                debouncedExitDumbMode.run()
            }
        })
        messageBus!!.subscribe(ProjectTopics.PROJECT_ROOTS, commonRunnableListeners)
        messageBus!!.subscribe(RefreshStatusesListener.REFRESH_STATUSES, commonRunnableListeners)
        messageBus!!.subscribe(ProjectTopics.MODULES, commonRunnableListeners)
        working = true
    }

    /** Disable manager. */
    private fun disable() {
        invalidateCache(project)
        virtualFileManager.removeVirtualFileListener(virtualFileListener)
        settings.removeListener(settingsListener)
        if (messageBus != null) {
            messageBus!!.disconnect()
            messageBus = null
        }
        working = false
    }

    /** Dispose and disable component. */
    override fun disposeComponent() {
        disable()
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

    /** [Runnable] implementation to rebuild [.confirmedIgnoredFiles]. */
    internal inner class RefreshTrackedIgnoredRunnable : Runnable, RefreshTrackedIgnoredListener {
        /** Default [Runnable] run method that invokes rebuilding with bus event propagating. */
        override fun run() {
            run(false)
        }

        /** Rebuilds [.confirmedIgnoredFiles] map in silent mode. */
        override fun refresh() {
            this.run(true)
        }

        /**
         * Rebuilds [.confirmedIgnoredFiles] map.
         *
         * @param silent propagate [IgnoreManager.TrackedIgnoredListener.TRACKED_IGNORED] event
         */
        fun run(silent: Boolean) {
            val result = ContainerUtil.newConcurrentMap<VirtualFile, VcsRoot>()
            for (vcsRoot in vcsRoots) {
                if (!Utils.isGitPluginEnabled || vcsRoot.vcs !is GitVcs) {
                    continue
                }
                val root = vcsRoot.path
                for (path in getIgnoredFiles(vcsRoot)) {
                    val file = root.findFileByRelativePath(path!!)
                    if (file != null) {
                        result[file] = vcsRoot
                    }
                }
            }
            if (!silent && !result.isEmpty()) {
                project.messageBus.syncPublisher(TrackedIgnoredListener.TRACKED_IGNORED).handleFiles(result)
            }
            confirmedIgnoredFiles.clear()
            confirmedIgnoredFiles.putAll(result)
            notConfirmedIgnoredFiles.clear()
            debouncedStatusesChanged.run()
            // TODO:
//            for (pane in AbstractProjectViewPane.EP_NAME.extensionList) {
//                if (pane.treeBuilder != null) {
//                    pane.treeBuilder.queueUpdate()
//                }
//            }
        }
    }

    /** Listener bounded with [TrackedIgnoredListener.TRACKED_IGNORED] topic to inform about new entries. */
    interface TrackedIgnoredListener {
        fun handleFiles(files: ConcurrentMap<VirtualFile, VcsRoot>)

        companion object {
            /** Topic for detected tracked and indexed files. */
            val TRACKED_IGNORED = Topic.create("New tracked and indexed files detected", TrackedIgnoredListener::class.java)
        }
    }

    /**
     * Listener bounded with [RefreshTrackedIgnoredListener.TRACKED_IGNORED_REFRESH] topic to trigger tracked and
     * ignored files list.
     */
    fun interface RefreshTrackedIgnoredListener {
        fun refresh()

        companion object {
            /** Topic for refresh tracked and indexed files. */
            val TRACKED_IGNORED_REFRESH = Topic.create("New tracked and indexed files detected", RefreshTrackedIgnoredListener::class.java)
        }
    }

    fun interface RefreshStatusesListener {
        fun refresh()

        companion object {
            /** Topic to refresh files statuses using [MessageBusConnection]. */
            val REFRESH_STATUSES = Topic("Refresh files statuses", RefreshStatusesListener::class.java)
        }
    }

    /**
     * Unique name of this component. If there is another component with the same name or name is null internal
     * assertion will occur.
     *
     * @return the name of this component
     */
    @NonNls
    override fun getComponentName() = "IgnoreManager"

    companion object {
        /** List of all available [IgnoreFileType]. */
        private val FILE_TYPES = ContainerUtil.map(IgnoreBundle.LANGUAGES, IgnoreLanguage::fileType)

        /** List of filenames that require to be associated with specific [IgnoreFileType]. */
        val FILE_TYPES_ASSOCIATION_QUEUE: MutableMap<String, IgnoreFileType> = ContainerUtil.newConcurrentMap()

        /**
         * Returns [IgnoreManager] service instance.
         *
         * @param project current project
         * @return [instance][IgnoreManager]
         */
        fun getInstance(project: Project): IgnoreManager {
            return project.getComponent(IgnoreManager::class.java)
        }

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
                application.invokeLater({
                    application.runWriteAction {
                        fileTypeManager.associate(fileType, ExactFileNameMatcher(fileName))
                        FILE_TYPES_ASSOCIATION_QUEUE.remove(fileName)
                    }
                }, ModalityState.NON_MODAL)
            } else if (!FILE_TYPES_ASSOCIATION_QUEUE.containsKey(fileName)) {
                FILE_TYPES_ASSOCIATION_QUEUE[fileName] = fileType
            }
        }
    }
}
