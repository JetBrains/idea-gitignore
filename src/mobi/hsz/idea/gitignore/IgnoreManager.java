/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore;

import com.intellij.ProjectTopics;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsRoot;
import com.intellij.openapi.vfs.*;
import com.intellij.util.Time;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import git4idea.GitVcs;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor;
import mobi.hsz.idea.gitignore.indexing.IgnoreEntryOccurrence;
import mobi.hsz.idea.gitignore.indexing.IgnoreFilesIndex;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.*;
import mobi.hsz.idea.gitignore.util.exec.ExternalExec;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import static mobi.hsz.idea.gitignore.IgnoreManager.RefreshTrackedIgnoredListener.TRACKED_IGNORED_REFRESH;
import static mobi.hsz.idea.gitignore.IgnoreManager.TrackedIgnoredListener.TRACKED_IGNORED;
import static mobi.hsz.idea.gitignore.settings.IgnoreSettings.KEY;

/**
 * {@link IgnoreManager} handles ignore files indexing and status caching.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0
 */
public class IgnoreManager implements DumbAware, ProjectComponent {
    /** Current project. */
    private final Project project;

    /** List of all available {@link IgnoreFileType}. */
    private static final List<IgnoreFileType> FILE_TYPES =
            ContainerUtil.map(IgnoreBundle.LANGUAGES, IgnoreLanguage::getFileType);

    /** List of filenames that require to be associated with specific {@link IgnoreFileType}. */
    public static final Map<String, IgnoreFileType> FILE_TYPES_ASSOCIATION_QUEUE = ContainerUtil.newConcurrentMap();

    /** {@link MatcherUtil} instance. */
    @NotNull
    private final MatcherUtil matcher;

    /** {@link VirtualFileManager} instance. */
    @NotNull
    private final VirtualFileManager virtualFileManager;

    /** {@link IgnoreSettings} instance. */
    @NotNull
    private final IgnoreSettings settings;

    /** {@link FileStatusManager} instance. */
    @NotNull
    private final FileStatusManager statusManager;

    /** {@link ProjectLevelVcsManager} instance. */
    @NotNull
    private final ProjectLevelVcsManager projectLevelVcsManager;

    /** {@link RefreshTrackedIgnoredRunnable} instance. */
    @NotNull
    private final RefreshTrackedIgnoredRunnable refreshTrackedIgnoredRunnable;

    /** Common runnable listeners. */
    @NotNull
    private final CommonRunnableListeners commonRunnableListeners;

    /** {@link MessageBusConnection} instance. */
    @Nullable
    private MessageBusConnection messageBus;

    /** List of the files that are ignored and also tracked by Git. */
    @NotNull
    private final ConcurrentMap<VirtualFile, VcsRoot> confirmedIgnoredFiles = ContainerUtil.createConcurrentWeakMap();

    /** List of the new files that were not covered by {@link #confirmedIgnoredFiles} yet. */
    @NotNull
    private final HashSet<VirtualFile> notConfirmedIgnoredFiles = new HashSet<>();

    /** References to the indexed {@link IgnoreEntryOccurrence}. */
    @NotNull
    private final CachedConcurrentMap<IgnoreFileType, Collection<IgnoreEntryOccurrence>> cachedIgnoreFilesIndex;

    /** References to the indexed outer files. */
    @NotNull
    private final CachedConcurrentMap<IgnoreFileType, Collection<VirtualFile>> cachedOuterFiles;

    @NotNull
    private final ExpiringMap<VirtualFile, Boolean> expiringStatusCache = new ExpiringMap<>(Time.SECOND);

    /** {@link FileStatusManager#fileStatusesChanged()} method wrapped with {@link Debounced}. */
    private final Debounced debouncedStatusesChanged = new Debounced(1000) {
        @Override
        protected void task(@Nullable Object argument) {
            expiringStatusCache.clear();
            statusManager.fileStatusesChanged();
        }
    };

    /** {@link FileStatusManager#fileStatusesChanged()} method wrapped with {@link Debounced}. */
    private final Debounced<Boolean> debouncedRefreshTrackedIgnores = new Debounced<Boolean>(1000) {
        @Override
        protected void task(@Nullable Boolean refresh) {
            if (Boolean.TRUE.equals(refresh)) {
                refreshTrackedIgnoredRunnable.refresh();
            } else {
                refreshTrackedIgnoredRunnable.run();
            }
        }
    };

    /** {@link DumbService.DumbModeListener#exitDumbMode()} method body wrapped with {@link Debounced}. */
    private final Debounced<Boolean> debouncedExitDumbMode = new Debounced<Boolean>(3000) {
        @Override
        protected void task(@Nullable Boolean refresh) {
            cachedIgnoreFilesIndex.clear();
            for (Map.Entry<String, IgnoreFileType> entry : FILE_TYPES_ASSOCIATION_QUEUE.entrySet()) {
                associateFileType(entry.getKey(), entry.getValue());
            }
            debouncedStatusesChanged.run();
        }
    };

    /** Scheduled feature connected with {@link #debouncedRefreshTrackedIgnores}. */
    @NotNull
    private final InterruptibleScheduledFuture refreshTrackedIgnoredFeature;

    /** {@link IgnoreManager} working flag. */
    private boolean working;

    /** List of available VCS roots for the current project. */
    @NotNull
    private final List<VcsRoot> vcsRoots = ContainerUtil.newArrayList();

    /** {@link VirtualFileListener} instance to check if file's content was changed. */
    @NotNull
    private final VirtualFileListener virtualFileListener = new VirtualFileListener() {
        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
            handleEvent(event);
        }

        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
            handleEvent(event);
            notConfirmedIgnoredFiles.add(event.getFile());
            debouncedRefreshTrackedIgnores.run(true);
        }

        @Override
        public void fileDeleted(@NotNull VirtualFileEvent event) {
            handleEvent(event);
            notConfirmedIgnoredFiles.add(event.getFile());
            debouncedRefreshTrackedIgnores.run(true);
        }

        @Override
        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
            handleEvent(event);
            notConfirmedIgnoredFiles.add(event.getFile());
            debouncedRefreshTrackedIgnores.run(true);
        }

        @Override
        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
            handleEvent(event);
            notConfirmedIgnoredFiles.add(event.getFile());
            debouncedRefreshTrackedIgnores.run(true);
        }

        private void handleEvent(@NotNull VirtualFileEvent event) {
            final FileType fileType = event.getFile().getFileType();
            if (fileType instanceof IgnoreFileType) {
                cachedIgnoreFilesIndex.remove((IgnoreFileType) fileType);
                cachedOuterFiles.remove((IgnoreFileType) fileType);

                if (fileType instanceof GitExcludeFileType) {
                    cachedOuterFiles.remove(GitFileType.INSTANCE);
                }
                expiringStatusCache.clear();
                debouncedStatusesChanged.run();
                debouncedRefreshTrackedIgnores.run();
            }
        }
    };

    /** {@link IgnoreSettings} listener to watch changes in the plugin's settings. */
    @NotNull
    private final IgnoreSettings.Listener settingsListener = new IgnoreSettings.Listener() {
        @Override
        public void onChange(@NotNull KEY key, Object value) {
            switch (key) {

                case IGNORED_FILE_STATUS:
                    toggle((Boolean) value);
                    break;

                case OUTER_IGNORE_RULES:
                case LANGUAGES:
                    IgnoreBundle.ENABLED_LANGUAGES.clear();
                    if (isEnabled()) {
                        if (working) {
                            debouncedStatusesChanged.run();
                            debouncedRefreshTrackedIgnores.run();
                        } else {
                            enable();
                        }
                    }
                    break;

                case HIDE_IGNORED_FILES:
                    ProjectView.getInstance(project).refresh();
                    break;

            }
        }
    };

    /**
     * Returns {@link IgnoreManager} service instance.
     *
     * @param project current project
     * @return {@link IgnoreManager instance}
     */
    @NotNull
    public static IgnoreManager getInstance(@NotNull final Project project) {
        return project.getComponent(IgnoreManager.class);
    }

    /**
     * Constructor builds {@link IgnoreManager} instance.
     *
     * @param project current project
     */
    public IgnoreManager(@NotNull final Project project) {
        this.project = project;
        this.cachedIgnoreFilesIndex = CachedConcurrentMap.create(key -> IgnoreFilesIndex.getEntries(project, key));
        this.cachedOuterFiles = CachedConcurrentMap.create(key -> key.getIgnoreLanguage().getOuterFiles(project));
        this.matcher = new MatcherUtil();
        this.virtualFileManager = VirtualFileManager.getInstance();
        this.settings = IgnoreSettings.getInstance();
        this.statusManager = FileStatusManager.getInstance(project);
        this.refreshTrackedIgnoredRunnable = new RefreshTrackedIgnoredRunnable();
        this.refreshTrackedIgnoredFeature =
                new InterruptibleScheduledFuture(debouncedRefreshTrackedIgnores, 10000, 5);
        this.refreshTrackedIgnoredFeature.setTrailing(true);
        this.projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);
        this.commonRunnableListeners = new CommonRunnableListeners(debouncedStatusesChanged);
    }

    /**
     * Returns {@link MatcherUtil} instance which is required for sharing matcher cache.
     *
     * @return {@link MatcherUtil} instance
     */
    @NotNull
    public MatcherUtil getMatcher() {
        return matcher;
    }

    /**
     * Checks if file is ignored.
     *
     * @param file current file
     * @return file is ignored
     */
    public boolean isFileIgnored(@NotNull final VirtualFile file) {
        final Boolean cached = expiringStatusCache.get(file);
        if (cached != null) {
            return cached;
        }
        if (ApplicationManager.getApplication().isDisposed() || project.isDisposed() ||
                DumbService.isDumb(project) || !isEnabled() || !Utils.isInProject(file, project) ||
                NoAccessDuringPsiEvents.isInsideEventProcessing()) {
            return false;
        }

        boolean ignored = false;
        boolean matched = false;
        int valuesCount = 0;

        for (IgnoreFileType fileType : FILE_TYPES) {
            ProgressManager.checkCanceled();
            if (!IgnoreBundle.ENABLED_LANGUAGES.get(fileType)) {
                continue;
            }

            final Collection<IgnoreEntryOccurrence> values = cachedIgnoreFilesIndex.get(fileType);

            valuesCount += values.size();
            for (IgnoreEntryOccurrence value : values) {
                ProgressManager.checkCanceled();
                String relativePath;
                final VirtualFile entryFile = value.getFile();
                if (entryFile == null) {
                    continue;
                } else if (fileType instanceof GitExcludeFileType) {
                    VirtualFile workingDirectory = GitExcludeFileType.getWorkingDirectory(project, entryFile);
                    if (workingDirectory == null || !Utils.isUnder(file, workingDirectory)) {
                        continue;
                    }
                    relativePath = Utils.getRelativePath(workingDirectory, file);
                } else {
                    final VirtualFile vcsRoot = getVcsRootFor(file);
                    if (vcsRoot != null && !Utils.isUnder(entryFile, vcsRoot)) {
                        if (!cachedOuterFiles.get(fileType).contains(entryFile)) {
                            continue;
                        }
                    }

                    if (ExternalIndexableSetContributor.getAdditionalFiles(project).contains(entryFile)) {
                        final VirtualFile projectDir = Utils.guessProjectDir(project);
                        relativePath = Utils.getRelativePath(projectDir, file);
                    } else {
                        relativePath = Utils.getRelativePath(entryFile.getParent(), file);
                    }
                }

                if (relativePath == null) {
                    continue;
                }
                relativePath = StringUtil.trimEnd(StringUtil.trimStart(relativePath, "/"), "/");
                if (StringUtil.isEmpty(relativePath)) {
                    continue;
                }

                if (file.isDirectory()) {
                    relativePath += "/";
                }

                for (Pair<String, Boolean> item : value.getItems()) {
                    final Pattern pattern = Glob.getPattern(item.first);
                    if (matcher.match(pattern, relativePath)) {
                        ignored = !item.second;
                        matched = true;
                    }
                }
            }
        }

        if (valuesCount > 0 && !ignored && !matched) {
            final VirtualFile directory = file.getParent();
            if (directory != null) {
                for (VcsRoot vcsRoot : vcsRoots) {
                    ProgressManager.checkCanceled();
                    if (directory.equals(vcsRoot.getPath())) {
                        return expiringStatusCache.set(file, false);
                    }
                }
                return expiringStatusCache.set(file, isFileIgnored(directory));
            }
        }

        if (ignored) {
            refreshTrackedIgnoredFeature.cancel();
        }

        return expiringStatusCache.set(file, ignored);
    }

    /**
     * Finds {@link VirtualFile} directory of {@link VcsRoot} that contains passed file.
     *
     * @param file to check
     * @return VCS Root for given file
     */
    @Nullable
    private VirtualFile getVcsRootFor(@NotNull final VirtualFile file) {
        final VcsRoot vcsRoot = ContainerUtil.find(
                ContainerUtil.reverse(vcsRoots),
                root -> root.getPath() != null && Utils.isUnder(file, root.getPath())
        );
        return vcsRoot != null ? vcsRoot.getPath() : null;
    }

    /**
     * Associates given file with proper {@link IgnoreFileType}.
     *
     * @param fileName to associate
     * @param fileType file type to bind with pattern
     */
    public static void associateFileType(@NotNull final String fileName, @NotNull final IgnoreFileType fileType) {
        final Application application = ApplicationManager.getApplication();
        if (application.isDispatchThread()) {
            final FileTypeManager fileTypeManager = FileTypeManager.getInstance();
            application.invokeLater(() -> application.runWriteAction(() -> {
                fileTypeManager.associate(fileType, new ExactFileNameMatcher(fileName));
                FILE_TYPES_ASSOCIATION_QUEUE.remove(fileName);
            }), ModalityState.NON_MODAL);
        } else if (!FILE_TYPES_ASSOCIATION_QUEUE.containsKey(fileName)) {
            FILE_TYPES_ASSOCIATION_QUEUE.put(fileName, fileType);
        }
    }

    /**
     * Checks if file is ignored and tracked.
     *
     * @param file current file
     * @return file is ignored and tracked
     */
    public boolean isFileTracked(@NotNull final VirtualFile file) {
        return !notConfirmedIgnoredFiles.contains(file) && !confirmedIgnoredFiles.isEmpty() &&
                confirmedIgnoredFiles.containsKey(file);
    }

    /**
     * Invoked when the project corresponding to this component instance is opened.<p> Note that components may be
     * created for even unopened projects and this method can be never invoked for a particular component instance (for
     * example for default project).
     */
    @Override
    public void projectOpened() {
        ExternalIndexableSetContributor.invalidateDisposedProjects();
        if (isEnabled() && !working) {
            enable();
        }
    }

    /**
     * Invoked when the project corresponding to this component instance is closed.<p> Note that components may be
     * created for even unopened projects and this method can be never invoked for a particular component instance (for
     * example for default project).
     */
    @Override
    public void projectClosed() {
        ExternalIndexableSetContributor.invalidateDisposedProjects();
        disable();
    }

    /**
     * Checks if ignored files watching is enabled.
     *
     * @return enabled
     */
    private boolean isEnabled() {
        return settings.isIgnoredFileStatus();
    }

    /** Enable manager. */
    private void enable() {
        if (working) {
            return;
        }

        refreshTrackedIgnoredFeature.run();
        virtualFileManager.addVirtualFileListener(virtualFileListener);
        settings.addListener(settingsListener);

        messageBus = project.getMessageBus().connect();

        messageBus.subscribe(TRACKED_IGNORED_REFRESH, () -> debouncedRefreshTrackedIgnores.run(true));

        messageBus.subscribe(ProjectLevelVcsManager.VCS_CONFIGURATION_CHANGED, () -> {
            ExternalIndexableSetContributor.invalidateCache(project);
            vcsRoots.clear();
            vcsRoots.addAll(ContainerUtil.newArrayList(projectLevelVcsManager.getAllVcsRoots()));
        });

        messageBus.subscribe(DumbService.DUMB_MODE, new DumbService.DumbModeListener() {
            @Override
            public void enteredDumbMode() {
            }

            @Override
            public void exitDumbMode() {
                debouncedExitDumbMode.run();
            }
        });

        messageBus.subscribe(ProjectTopics.PROJECT_ROOTS, commonRunnableListeners);
        messageBus.subscribe(RefreshStatusesListener.REFRESH_STATUSES, commonRunnableListeners);
        messageBus.subscribe(ProjectTopics.MODULES, commonRunnableListeners);

        working = true;
    }

    /** Disable manager. */
    private void disable() {
        ExternalIndexableSetContributor.invalidateCache(project);
        virtualFileManager.removeVirtualFileListener(virtualFileListener);
        settings.removeListener(settingsListener);

        if (messageBus != null) {
            messageBus.disconnect();
            messageBus = null;
        }

        working = false;
    }

    /** Dispose and disable component. */
    @Override
    public void disposeComponent() {
        disable();
    }

    /**
     * Runs {@link #enable()} or {@link #disable()} depending on the passed value.
     *
     * @param enable or disable
     */
    private void toggle(@NotNull Boolean enable) {
        if (enable) {
            enable();
        } else {
            disable();
        }
    }

    /** {@link Runnable} implementation to rebuild {@link #confirmedIgnoredFiles}. */
    class RefreshTrackedIgnoredRunnable implements Runnable, IgnoreManager.RefreshTrackedIgnoredListener {
        /** Default {@link Runnable} run method that invokes rebuilding with bus event propagating. */
        @Override
        public void run() {
            run(false);
        }

        /** Rebuilds {@link #confirmedIgnoredFiles} map in silent mode. */
        @Override
        public void refresh() {
            this.run(true);
        }

        /**
         * Rebuilds {@link #confirmedIgnoredFiles} map.
         *
         * @param silent propagate {@link IgnoreManager.TrackedIgnoredListener#TRACKED_IGNORED} event
         */
        public void run(boolean silent) {
            final ConcurrentMap<VirtualFile, VcsRoot> result = ContainerUtil.newConcurrentMap();
            for (VcsRoot vcsRoot : vcsRoots) {
                if (!(vcsRoot.getVcs() instanceof GitVcs) || vcsRoot.getPath() == null) {
                    continue;
                }
                final VirtualFile root = vcsRoot.getPath();
                for (String path : ExternalExec.getIgnoredFiles(vcsRoot)) {
                    final VirtualFile file = root.findFileByRelativePath(path);
                    if (file != null) {
                        result.put(file, vcsRoot);
                    }
                }
            }

            if (!silent && !result.isEmpty()) {
                project.getMessageBus().syncPublisher(TRACKED_IGNORED).handleFiles(result);
            }
            confirmedIgnoredFiles.clear();
            confirmedIgnoredFiles.putAll(result);
            notConfirmedIgnoredFiles.clear();
            debouncedStatusesChanged.run();

            for (AbstractProjectViewPane pane : AbstractProjectViewPane.EP_NAME.getExtensions()) {
                if (pane.getTreeBuilder() != null) {
                    pane.getTreeBuilder().queueUpdate();
                }
            }
        }
    }

    /** Listener bounded with {@link TrackedIgnoredListener#TRACKED_IGNORED} topic to inform about new entries. */
    public interface TrackedIgnoredListener {
        /** Topic for detected tracked and indexed files. */
        Topic<TrackedIgnoredListener> TRACKED_IGNORED =
                Topic.create("New tracked and indexed files detected", TrackedIgnoredListener.class);

        void handleFiles(@NotNull ConcurrentMap<VirtualFile, VcsRoot> files);
    }

    /**
     * Listener bounded with {@link RefreshTrackedIgnoredListener#TRACKED_IGNORED_REFRESH} topic to trigger tracked and
     * ignored files list.
     */
    public interface RefreshTrackedIgnoredListener {
        /** Topic for refresh tracked and indexed files. */
        Topic<RefreshTrackedIgnoredListener> TRACKED_IGNORED_REFRESH =
                Topic.create("New tracked and indexed files detected", RefreshTrackedIgnoredListener.class);

        void refresh();
    }

    public interface RefreshStatusesListener {
        /** Topic to refresh files statuses using {@link MessageBusConnection}. */
        Topic<RefreshStatusesListener> REFRESH_STATUSES =
                new Topic<>("Refresh files statuses", RefreshStatusesListener.class);

        void refresh();
    }

    /**
     * Unique name of this component. If there is another component with the same name or name is null internal
     * assertion will occur.
     *
     * @return the name of this component
     */
    @NonNls
    @NotNull
    @Override
    public String getComponentName() {
        return "IgnoreManager";
    }
}
