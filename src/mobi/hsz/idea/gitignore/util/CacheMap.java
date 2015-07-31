/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.util.containers.ConcurrentHashMap;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@link ConcurrentHashMap} cache helper.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0.2
 */
public class CacheMap {
    private final ConcurrentHashMap<IgnoreFile, Pair<Set<Integer>, List<Pair<Pattern, Boolean>>>> map = new ConcurrentHashMap<IgnoreFile, Pair<Set<Integer>, List<Pair<Pattern, Boolean>>>>();

    /** Cache {@link ConcurrentHashMap} to store files statuses. */
    private final HashMap<VirtualFile, Status> statuses = new HashMap<VirtualFile, Status>();

    /** Current project. */
    private final Project project;

    /** {@link FileStatusManager} instance. */
    private final FileStatusManager statusManager;

    /** Status of the file. */
    private enum Status {
        IGNORED, UNIGNORED, UNTOUCHED
    }

    public CacheMap(Project project) {
        this.project = project;
        this.statusManager = FileStatusManager.getInstance(project);
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
     * @param set entries hashCodes set
     */
    protected void add(@NotNull final IgnoreFile file, Set<Integer> set) {
        final List<Pair<Pattern, Boolean>> patterns = ContainerUtil.newArrayList();

        runVisitorInReadAction(file, new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                Pattern pattern = Glob.createPattern(entry);
                if (pattern != null) {
                    patterns.add(Pair.create(pattern, entry.isNegated()));
                }
            }
        });

        map.put(file, Pair.create(set, patterns));
        statuses.clear();
        statusManager.fileStatusesChanged();
    }

    /**
     * Checks if {@link IgnoreFile} has changed and rebuilds its cache.
     *
     * @param file to check
     */
    public void hasChanged(@NotNull IgnoreFile file) {
        final Pair<Set<Integer>, List<Pair<Pattern, Boolean>>> recent = map.get(file);

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
                if (virtualFile != null && ((VirtualFileWithId) virtualFile).getId() > 0) {
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

        if (!status.equals(Status.UNTOUCHED)) {
            return status.equals(Status.IGNORED);
        }

        final List<IgnoreFile> files = ContainerUtil.reverse(Utils.ignoreFilesSort(Collections.list(map.keys())));

        for (final IgnoreFile ignoreFile : files) {
            final VirtualFile ignoreFileParent = ignoreFile.getVirtualFile().getParent();
            if (ignoreFileParent == null) {
                continue;
            }

            boolean outer = ignoreFile.isOuter();
            if (!outer) {
                if (!Utils.isUnder(file, ignoreFileParent)) {
                    continue;
                }

                VirtualFile vcsRoot = ProjectLevelVcsManager.getInstance(project).getVcsRootFor(file);
                if (vcsRoot != null && !vcsRoot.equals(file) && !Utils.isUnder(ignoreFile.getVirtualFile(), vcsRoot)) {
                    continue;
                }
            }

            final String path = Utils.getRelativePath(outer ? project.getBaseDir() : ignoreFileParent, file);
            if (StringUtil.isEmpty(path)) {
                continue;
            }

            List<Pair<Pattern, Boolean>> patterns = map.get(ignoreFile).getSecond();
            for (Pair<Pattern, Boolean> pair : ContainerUtil.reverse(patterns)) {
                if (pair.getFirst().matcher(path).matches()) {
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
     * Returns the status of the parent.
     *
     * @param file to check
     * @return any of the parents is ignored
     */
    @NotNull
    protected Status getParentStatus(@NotNull VirtualFile file) {
        VirtualFile parent = file.getParent();
        VirtualFile vcsRoot = ProjectLevelVcsManager.getInstance(project).getVcsRootFor(file);

        while (parent != null && !parent.equals(project.getBaseDir()) && (vcsRoot == null || !vcsRoot.equals(parent))) {
            if (statuses.containsKey(parent) && statuses.get(parent) != null) {
                return statuses.get(parent);
            }
            parent = parent.getParent();
        }
        return Status.UNTOUCHED;
    }

    /**
     * Clears cache.
     */
    public void clear() {
        map.clear();
        statuses.clear();
        statusManager.fileStatusesChanged();
    }

    /**
     * Wrapper for {link #map.remove}.
     *
     * @param file to remove
     * @return removed value
     */
    public Pair<Set<Integer>, List<Pair<Pattern, Boolean>>> remove(IgnoreFile file) {
        return map.remove(file);
    }
}
