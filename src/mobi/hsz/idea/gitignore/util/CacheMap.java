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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ConcurrentHashMap;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashMap;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * {@link ConcurrentHashMap} cache helper.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0.2
 */
public class CacheMap {
    private final ConcurrentHashMap<IgnoreFile, Trinity<Set<Integer>, Set<String>, Set<String>>> map = new ConcurrentHashMap<IgnoreFile, Trinity<Set<Integer>, Set<String>, Set<String>>>();

    /** Cache {@link ConcurrentHashMap} to store files statuses. */
    private final HashMap<VirtualFile, Boolean> statuses = new HashMap<VirtualFile, Boolean>();

    /** Current project. */
    private final Project project;

    /** {@link FileStatusManager} instance. */
    private final FileStatusManager statusManager;

    public CacheMap(Project project) {
        this.project = project;
        this.statusManager = FileStatusManager.getInstance(project);
    }

    /**
     * Adds new {@link IgnoreFile} to the cache and builds its hashCode and patterns sets.
     *
     * @param file to add
     */
    public void add(@NotNull IgnoreFile file) {
        final Set<Integer> set = ContainerUtil.newHashSet();

        file.acceptChildren(new IgnoreVisitor() {
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
    public void add(@NotNull IgnoreFile file, Set<Integer> set) {
        final Set<String> ignored = ContainerUtil.newHashSet();
        final Set<String> unignored = ContainerUtil.newHashSet();
        final VirtualFile parent = file.getVirtualFile().getParent();

        file.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                List<String> matched = Glob.findAsPaths(parent, entry, true);
                if (!entry.isNegated()) {
                    ignored.addAll(matched);
                    unignored.removeAll(matched);
                } else {
                    unignored.addAll(matched);
                    ignored.removeAll(matched);
                }
            }
        });

        map.put(file, Trinity.create(set, ignored, unignored));
    }

    /**
     * Checks if {@link IgnoreFile} has changed and rebuilds its cache.
     *
     * @param file to check
     */
    public void hasChanged(@NotNull IgnoreFile file) {
        final Trinity<Set<Integer>, Set<String>, Set<String>> recent = map.get(file);

        final Set<Integer> set = ContainerUtil.newHashSet();
        file.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                set.add(entry.getText().trim().hashCode());
            }
        });

        if (recent == null || !set.equals(recent.getFirst())) {
            add(file, set);
            statusManager.fileStatusesChanged();
        }
    }

    /**
     * Checks if given {@link VirtualFile} is ignored.
     *
     * @param file to check
     * @return file is ignored
     */
    public boolean isFileIgnored(@NotNull VirtualFile file) {
        boolean result = false;

        final List<IgnoreFile> files = Collections.list(map.keys());
        ContainerUtil.sort(files, new Comparator<IgnoreFile>() {
            @Override
            public int compare(IgnoreFile file1, IgnoreFile file2) {
                return StringUtil.naturalCompare(file1.getVirtualFile().getPath(), file2.getVirtualFile().getPath());
            }
        });

        for (final IgnoreFile ignoreFile : files) {
            final VirtualFile ignoreFileParent = ignoreFile.getVirtualFile().getParent();
            if (!Utils.isUnder(file, ignoreFileParent)) {
                continue;
            }

            final String path = Utils.getRelativePath(ignoreFileParent, file);
            if (StringUtil.isEmpty(path)) {
                continue;
            }

            Set<String> ignored = map.get(ignoreFile).getSecond();
            Set<String> unignored = map.get(ignoreFile).getThird();

            if (ignored.contains(path)) {
                result = true;
            } else if (unignored.contains(path)) {
                result = false;
            }
        }

        statuses.put(file, result);
        return result;
    }

    /**
     * Checks if any of the file parents is ignored.
     *
     * @param file to check
     * @return any of the parents is ignored
     */
    public boolean isParentIgnored(@NotNull VirtualFile file) {
        VirtualFile parent = file.getParent();
        while (parent != null && !parent.equals(project.getBaseDir())) {
            if (statuses.containsKey(parent) && statuses.get(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Clears cache.
     */
    public void clear() {
        this.map.clear();
        this.statuses.clear();
    }

    /**
     * Wrapper for {link #map.remove}.
     *
     * @param file to remove
     * @return removed value
     */
    public Trinity<Set<Integer>, Set<String>, Set<String>> remove(IgnoreFile file) {
        return map.remove(file);
    }
}
