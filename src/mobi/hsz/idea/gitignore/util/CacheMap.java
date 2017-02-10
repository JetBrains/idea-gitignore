/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.util;

import com.intellij.concurrency.JobScheduler;
import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.messages.MessageBus;
import git4idea.repo.GitRepository;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.exec.ExternalExec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.hsz.idea.gitignore.IgnoreManager.TrackedIgnoredListener.TRACKED_IGNORED;

/**
 * {@link HashMap} cache helper.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0.2
 */
public class CacheMap {
    /** Main cache map instance. */
    @NotNull
    private final ConcurrentMap<IgnoreFile, Pair<Set<Integer>, List<Pair<Matcher, Boolean>>>> map = ContainerUtil.newConcurrentMap();

    /** Cache {@link HashMap} to store files statuses. */
    @NotNull
    private final HashMap<VirtualFile, Status> statuses = new HashMap<VirtualFile, Status>();

    /** List of the files that are ignored and also tracked by Git. */
    @NotNull
    private final HashMap<VirtualFile, Repository> trackedIgnoredFiles = new HashMap<VirtualFile, Repository>();

    /** Current project. */
    @NotNull
    private final Project project;

    /** {@link FileStatusManager} instance. */
    @NotNull
    private final FileStatusManager statusManager;

    /** {@link MessageBus} instance. */
    @NotNull
    private final MessageBus messageBus;

    /** {@link VcsRepositoryManager} instance. */
    @NotNull
    private VcsRepositoryManager vcsRepositoryManager;
    
    /** Task to fetch tracked and ignored files using Git repositories. */
    @NotNull
    private Runnable trackedIgnoredFilesRunnable = new Runnable() {
        @Override
        public void run() {
            final Collection<Repository> repositories = vcsRepositoryManager.getRepositories();
            final HashMap<VirtualFile, Repository> result = new HashMap<VirtualFile, Repository>();
            for (Repository repository : repositories) {
                if (!(repository instanceof GitRepository)) {
                    continue;
                }
                VirtualFile root = repository.getRoot();
                for (String path : ExternalExec.getTrackedIgnoredFiles(repository)) {
                    final VirtualFile file = root.findFileByRelativePath(path);
                    result.put(file, repository);
                }
            }

            if (!result.isEmpty()) {
                messageBus.syncPublisher(TRACKED_IGNORED).handleFiles(result);
            }
            trackedIgnoredFiles.clear();
            trackedIgnoredFiles.putAll(result);
            statusManager.fileStatusesChanged();

            for (AbstractProjectViewPane pane : Extensions.getExtensions(AbstractProjectViewPane.EP_NAME, project)) {
                pane.getTreeBuilder().queueUpdate();
            }
        }
    };
    
    /** Timer for {@link #trackedIgnoredFilesRunnable}. */
    @Nullable
    private ScheduledFuture<?> trackedIgnoredFilesTimer;

    /** Status of the file. */
    private enum Status {
        IGNORED, UNIGNORED, UNTOUCHED
    }

    /** Constructor. */
    public CacheMap(@NotNull Project project) {
        this.project = project;
        this.statusManager = FileStatusManager.getInstance(project);
        this.vcsRepositoryManager = VcsRepositoryManager.getInstance(project);
        this.messageBus = project.getMessageBus();
    }

    /**
     * Adds new {@link IgnoreFile} to the cache and builds its hashCode and patterns sets.
     *
     * @param file to add
     */
    public void add(@NotNull final IgnoreFile file) {
        final Set<Integer> set = ContainerUtil.newHashSet();

        runVisitorInReadAction(file, new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                set.add(entry.getText().trim().hashCode());
            }
        });

        add(file, set);
    }

    /**
     * Adds new {@link IgnoreFile} to the cache and builds its hashCode and patterns sets.
     *
     * @param file to add
     * @param set  entries hashCodes set
     */
    private void add(@NotNull final IgnoreFile file, @NotNull Set<Integer> set) {
        final List<Pair<Matcher, Boolean>> matchers = ContainerUtil.newArrayList();

        runVisitorInReadAction(file, new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                Pattern pattern = Glob.createPattern(entry);
                if (pattern != null) {
                    matchers.add(Pair.create(pattern.matcher(""), entry.isNegated()));
                }
            }
        });

        map.put(file, Pair.create(set, matchers));
        refresh();
    }

    /**
     * Looks for given {@link VirtualFile} and removes it from the cache map.
     * 
     * @param file to remove
     */
    public void cleanup(@NotNull VirtualFile file) {
        for (IgnoreFile ignoreFile : map.keySet()) {
            if (ignoreFile.getVirtualFile().equals(file)) {
                map.remove(ignoreFile);
            }
        }
        refresh();
    }
    
    /** Clears cache. */
    public void clear() {
        map.clear();
        refresh();
    }
    
    /** Refreshes statuses and {@link #trackedIgnoredFiles} list. */
    private void refresh() {
        statuses.clear();

        fetchTrackedIgnoredFiles();
        statusManager.fileStatusesChanged();
    }
    
    /**
     * Method loops over {@link GitRepository} repositories and checks if they contain any of
     * tracked files that are also ignored with .gitignore files.
     */
    private void fetchTrackedIgnoredFiles() {
        if (trackedIgnoredFilesTimer != null) {
            trackedIgnoredFilesTimer.cancel(false);
        }

        trackedIgnoredFilesTimer = JobScheduler.getScheduler().schedule(
                trackedIgnoredFilesRunnable,
                2000,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Checks if {@link IgnoreFile} has changed and rebuilds its cache.
     *
     * @param file to check
     */
    public void hasChanged(@NotNull IgnoreFile file) {
        final Pair<Set<Integer>, List<Pair<Matcher, Boolean>>> recent = map.get(file);

        final Set<Integer> set = ContainerUtil.newHashSet();
        file.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                set.add(entry.getText().trim().hashCode());
            }
        });

        if (recent == null || !set.equals(recent.getFirst())) {
            add(file, set);
        }
    }

    /**
     * Simple wrapper for running read action
     *
     * @param file    {@link IgnoreFile} to run visitor on it
     * @param visitor {@link IgnoreVisitor}
     */
    private void runVisitorInReadAction(@NotNull final IgnoreFile file, @NotNull final IgnoreVisitor visitor) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                VirtualFile virtualFile = file.getVirtualFile();
                if (virtualFile != null && (virtualFile instanceof LightVirtualFile
                        || (virtualFile instanceof VirtualFileWithId && ((VirtualFileWithId) virtualFile).getId() > 0))) {
                    file.acceptChildren(visitor);
                }
            }
        });
    }

    /**
     * Checks if given {@link VirtualFile} is ignored.
     *
     * @param file to check
     * @return file is ignored
     */
    public boolean isFileIgnored(@NotNull VirtualFile file) {
        Status status = statuses.get(file);

        if (status == null || status.equals(Status.UNTOUCHED)) {
            status = getParentStatus(file);
            statuses.put(file, status);
        }

        final List<IgnoreFile> files = ContainerUtil.reverse(Utils.ignoreFilesSort(ContainerUtil.newArrayList(map.keySet())));
        final ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);

        for (final IgnoreFile ignoreFile : files) {
            boolean outer = ignoreFile.isOuter();
            final VirtualFile ignoreFileParent = ignoreFile.getVirtualFile().getParent();

            if (!outer) {
                if (ignoreFileParent == null || !Utils.isUnder(file, ignoreFileParent)) {
                    continue;
                }

                VirtualFile vcsRoot = projectLevelVcsManager.getVcsRootFor(file);
                if (vcsRoot != null && !vcsRoot.equals(file) && !Utils.isUnder(ignoreFile.getVirtualFile(), vcsRoot)) {
                    continue;
                }
            }

            final String path = Utils.getRelativePath(outer ? project.getBaseDir() : ignoreFileParent, file);
            if (StringUtil.isEmpty(path)) {
                continue;
            }

            List<Pair<Matcher, Boolean>> matchers = map.get(ignoreFile).getSecond();
            for (Pair<Matcher, Boolean> pair : ContainerUtil.reverse(matchers)) {
                if (MatcherUtil.match(pair.getFirst(), path)) {
                    status = pair.getSecond() ? Status.UNIGNORED : Status.IGNORED;
                    break;
                }
            }

            if (!status.equals(Status.UNTOUCHED)) {
                break;
            }
        }

        statuses.put(file, status);
        return status.equals(Status.IGNORED);
    }

    /**
     * Checks if given {@link VirtualFile} is ignored and still tracked.
     *
     * @param file to check
     * @return file is ignored and tracked
     */
    public boolean isFileTrackedIgnored(@NotNull VirtualFile file) {
        return trackedIgnoredFiles.keySet().contains(file);
    }

    /**
     * Returns the status of the parent.
     *
     * @param file to check
     * @return any of the parents is ignored
     */
    @NotNull
    private Status getParentStatus(@NotNull VirtualFile file) {
        VirtualFile parent = file.getParent();
        VirtualFile vcsRoot = ProjectLevelVcsManager.getInstance(project).getVcsRootFor(file);

        while (parent != null && !parent.equals(project.getBaseDir()) && (vcsRoot == null || !vcsRoot.equals(parent))) {
            final Status status = statuses.get(parent);
            if (status != null) {
                return status;
            }
            parent = parent.getParent();
        }
        return Status.UNTOUCHED;
    }
}
