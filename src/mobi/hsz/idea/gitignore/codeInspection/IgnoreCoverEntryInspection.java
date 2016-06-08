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

package mobi.hsz.idea.gitignore.codeInspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.Glob;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Inspection tool that checks if entries are covered by others.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreCoverEntryInspection extends LocalInspectionTool {
    /** Cache key separator. */
    private static final String SEPARATOR = "$";

    /** Cache map to store handled entries' paths. */
    private final ConcurrentMap<String, Set<String>> cacheMap;

    /** {@link VirtualFileManager} instance. */
    private final VirtualFileManager virtualFileManager;

    /** Watches for the changes in the files tree and triggers the cache clear. */
    private final VirtualFileListener virtualFileListener = new VirtualFileAdapter() {
        @Override
        public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
            if (event.getPropertyName().equals("name")) {
                cacheMap.clear();
            }
        }

        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
            cacheMap.clear();
        }

        @Override
        public void fileDeleted(@NotNull VirtualFileEvent event) {
            cacheMap.clear();
        }

        @Override
        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
            cacheMap.clear();
        }

        @Override
        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
            cacheMap.clear();
        }
    };

    /**
     * Builds a new instance of {@link IgnoreCoverEntryInspection}.
     * Initializes {@link VirtualFileManager} and listens for the changes in the files tree.
     */
    public IgnoreCoverEntryInspection() {
        cacheMap = ContainerUtil.newConcurrentMap();
        virtualFileManager = VirtualFileManager.getInstance();
        virtualFileManager.addVirtualFileListener(virtualFileListener);
    }

    /**
     * Unregisters {@link #virtualFileListener} and clears the paths cache.
     *
     * @param project current project
     */
    @Override
    public void cleanup(@NotNull Project project) {
        virtualFileManager.removeVirtualFileListener(virtualFileListener);
        cacheMap.clear();
    }

    /**
     * Reports problems at file level. Checks if entries are covered by other entries.
     *
     * @param file       current working file to check
     * @param manager    {@link InspectionManager} to ask for {@link ProblemDescriptor}'s from
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return <code>null</code> if no problems found or not applicable at file level
     */
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        final VirtualFile virtualFile = file.getVirtualFile();
        if (!(file instanceof IgnoreFile) || !Utils.isInProject(virtualFile, file.getProject())) {
            return null;
        }

        final VirtualFile contextDirectory = virtualFile.getParent();
        if (contextDirectory == null) {
            return null;
        }

        final Set<String> ignored = ContainerUtil.newHashSet();
        final Set<String> unignored = ContainerUtil.newHashSet();

        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        final List<Pair<IgnoreEntry, IgnoreEntry>> entries = ContainerUtil.newArrayList();
        final Map<IgnoreEntry, Set<String>> map = ContainerUtil.newHashMap();

        file.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                Set<String> matched = getPathsSet(contextDirectory, entry);
                Collection<String> intersection;
                boolean modified;

                if (!entry.isNegated()) {
                    ignored.addAll(matched);
                    intersection = Utils.intersection(unignored, matched);
                    modified = unignored.removeAll(intersection);
                } else {
                    unignored.addAll(matched);
                    intersection = Utils.intersection(ignored, matched);
                    modified = ignored.removeAll(intersection);
                }

                if (modified) {
                    return;
                }

                for (IgnoreEntry recent : map.keySet()) {
                    Set<String> recentValues = map.get(recent);
                    if (recentValues.isEmpty() || matched.isEmpty()) {
                        continue;
                    }

                    if (entry.isNegated() == recent.isNegated()) {
                        if (recentValues.containsAll(matched)) {
                            entries.add(Pair.create(recent, entry));
                        } else if (matched.containsAll(recentValues)) {
                            entries.add(Pair.create(entry, recent));
                        }
                    } else {
                        if (intersection.containsAll(recentValues)) {
                            entries.add(Pair.create(entry, recent));
                        }
                    }
                }

                map.put(entry, matched);
            }
        });

        for (Pair<IgnoreEntry, IgnoreEntry> pair : entries) {
            problemsHolder.registerProblem(pair.second, message(pair.first, virtualFile, isOnTheFly),
                    new IgnoreRemoveEntryFix(pair.second));
        }

        return problemsHolder.getResultsArray();
    }

    /**
     * Returns the paths list for the given {@link IgnoreEntry} in {@link VirtualFile} context.
     * Stores fetched data in {@link #cacheMap} to limit the queries to the files tree.
     *
     * @param contextDirectory current context
     * @param entry            to check
     * @return paths list
     */
    private Set<String> getPathsSet(VirtualFile contextDirectory, IgnoreEntry entry) {
        final String key = contextDirectory.getPath() + SEPARATOR + entry.getText();
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, ContainerUtil.newHashSet(Glob.findAsPaths(contextDirectory, entry, true)));
        }
        return cacheMap.get(key);
    }

    /**
     * Helper for inspection message generating.
     *
     * @param coveringEntry entry that covers message related
     * @param virtualFile   current working file
     * @param onTheFly      true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return generated message {@link String}
     */
    private static String message(@NotNull IgnoreEntry coveringEntry, @NotNull VirtualFile virtualFile, boolean onTheFly) {
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (onTheFly || document == null) {
            return IgnoreBundle.message("codeInspection.coverEntry.message", "\'" + coveringEntry.getText() + "\'");
        }

        int startOffset = coveringEntry.getTextRange().getStartOffset();
        return IgnoreBundle.message("codeInspection.coverEntry.message",
                "<a href=\"" + virtualFile.getUrl() + "#" + startOffset + "\">" + coveringEntry.getText() + "</a>");
    }

    /**
     * Forces checking every entry in checked file.
     *
     * @return <code>true</code>
     */
    @Override
    public boolean runForWholeFile() {
        return true;
    }
}
