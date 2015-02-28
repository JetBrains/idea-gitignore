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

package mobi.hsz.idea.gitignore;

import com.google.common.collect.Sets;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.HashMap;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.Glob;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * {@link IgnoreManager} handles ignore files indexing and status caching.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.10
 */
public class IgnoreManager extends AbstractProjectComponent {
    private final IgnoreCacheMap cache;
    private final FileStatusManager statusManager;
    private final VirtualFile baseDir;
    private final PsiManagerImpl psiManager;
    private final VirtualFileManager virtualFileManager;

    private final VirtualFileListener virtualFileListener = new VirtualFileAdapter() {
        public boolean wasIgnoreFileType;

        /**
         * Fired when a virtual file is renamed from within IDEA, or its writable status is changed.
         * For files renamed externally, {@link #fileCreated} and {@link #fileDeleted} events will be fired.
         *
         * @param event the event object containing information about the change.
         */
        @Override
        public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
            if (event.getPropertyName().equals("name")) {
                boolean isIgnoreFileType = isIgnoreFileType(event);
                if (isIgnoreFileType && !wasIgnoreFileType) {
                    addFile(event);
                } else if (!isIgnoreFileType && wasIgnoreFileType) {
                    removeFile(event);
                }
            }
        }

        /**
         * Fired before the change of a name or writable status of a file is processed.
         *
         * @param event the event object containing information about the change.
         */
        @Override
        final public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
            wasIgnoreFileType = isIgnoreFileType(event);
        }

        /**
         * Fired when a virtual file is created. This event is not fired for files discovered during initial VFS initialization.
         *
         * @param event the event object containing information about the change.
         */
        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
            addFile(event);
        }

        @Override
        public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
            removeFile(event);
        }

        /**
         * Fired when a virtual file is copied from within IDEA.
         *
         * @param event the event object containing information about the change.
         */
        @Override
        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
            addFile(event);
        }

        /**
         * Adds {@link IgnoreFile} to the {@link IgnoreCacheMap}.
         */
        private void addFile(VirtualFileEvent event) {
            if (isIgnoreFileType(event)) {
                IgnoreFile file = getIgnoreFile(event.getFile());
                if (file != null) {
                    cache.add(file);
                }
            }
        }

        /**
         * Removes {@link IgnoreFile} from the {@link IgnoreCacheMap}.
         */
        private void removeFile(VirtualFileEvent event) {
            if (isIgnoreFileType(event)) {
                cache.remove(getIgnoreFile(event.getFile()));
            }
        }

        /**
         * Checks if event was fired on the {@link IgnoreFileType} file.
         *
         * @param event current event
         * @return event called on {@link IgnoreFileType}
         */
        protected boolean isIgnoreFileType(VirtualFileEvent event) {
            return event.getFile().getFileType() instanceof IgnoreFileType;
        }
    };

    private final com.intellij.psi.PsiTreeChangeListener psiTreeChangeListener = new PsiTreeChangeAdapter() {
        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
            if (event.getParent() instanceof IgnoreFile) {
                cache.hasChanged((IgnoreFile) event.getParent());
            }
        }
    };

    /**
     * Returns {@link IgnoreManager} service instance.
     *
     * @param project current project
     * @return {@link IgnoreManager instance}
     */
    public static IgnoreManager getInstance(Project project) {
        return project.getComponent(IgnoreManager.class);
    }

    /**
     * Constructor builds {@link IgnoreManager} instance.
     *
     * @param project current project
     */
    public IgnoreManager(@NotNull final Project project) {
        super(project);
        this.cache = new IgnoreCacheMap();
        this.statusManager = FileStatusManager.getInstance(project);
        this.baseDir = project.getBaseDir();
        this.psiManager = (PsiManagerImpl) PsiManager.getInstance(project);
        this.virtualFileManager = VirtualFileManager.getInstance();
    }

    /**
     * Helper for fetching {@link IgnoreFile} using {@link VirtualFile}.
     *
     * @param file current file
     * @return {@link IgnoreFile}
     */
    @Nullable
    private IgnoreFile getIgnoreFile(@NotNull VirtualFile file) {
        if (!file.exists()) {
            return null;
        }
        return (IgnoreFile) psiManager.findFile(file);
    }

    /**
     * Checks if file is ignored.
     *
     * @param file current file
     * @return file is ignored
     */
    public boolean isFileIgnored(final VirtualFile file) {
        return isParentIgnored(file) || cache.isFileIgnored(file);
    }

    /**
     * Checks if parent directory is ignored.
     *
     * @param file current file
     * @return parent is ignored
     */
    public boolean isParentIgnored(final VirtualFile file) {
        return cache.isParentIgnored(file);
    }

    /**
     * Invoked when the project corresponding to this component instance is opened.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component instance (for example for default project).
     */
    @Override
    public void projectOpened() {
        this.virtualFileManager.addVirtualFileListener(virtualFileListener);
        this.psiManager.addPsiTreeChangeListener(psiTreeChangeListener);

        final Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                if (((FileManagerImpl) psiManager.getFileManager()).isInitialized()) {
                    timer.cancel();

                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        public void run() {
                            GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);
                            for (IgnoreFileType type : IgnoreBundle.FILE_TYPES) {
                                for (VirtualFile virtualFile : FileTypeIndex.getFiles(type, scope)) {
                                    IgnoreFile file = getIgnoreFile(virtualFile);
                                    if (file != null) {
                                        cache.add(file);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }, 0, 200);
    }

    /**
     * Invoked when the project corresponding to this component instance is closed.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component instance (for example for default project).
     */
    @Override
    public void projectClosed() {
        this.virtualFileManager.removeVirtualFileListener(virtualFileListener);
        this.psiManager.removePsiTreeChangeListener(psiTreeChangeListener);
        this.cache.clear();
    }

    /**
     * Unique name of this component. If there is another component with the same name or
     * name is null internal assertion will occur.
     *
     * @return the name of this component
     */
    @NonNls
    @NotNull
    @Override
    public String getComponentName() {
        return "IgnoreManager";
    }

    /**
     * {@link HashMap} cache helper.
     */
    public class IgnoreCacheMap extends HashMap<IgnoreFile, Pair<Set<Integer>, Set<Pattern>>> {
        /** Cache {@link HashMap} to store files statuses. */
        private final HashMap<VirtualFile, Boolean> statuses = new HashMap<VirtualFile, Boolean>();

        /**
         * Adds new {@link IgnoreFile} to the cache and builds its hashCode and patterns sets.
         *
         * @param file to add
         */
        public void add(@NotNull IgnoreFile file) {
            final Set<Integer> set = Sets.newHashSet();
            final Set<Pattern> patterns = Sets.newHashSet();

            file.acceptChildren(new IgnoreVisitor() {
                @Override
                public void visitEntry(@NotNull IgnoreEntry entry) {
                    Pattern pattern = Glob.createPattern(entry);
                    if (pattern == null) {
                        return;
                    }
                    set.add(entry.getText().trim().hashCode());
                    patterns.add(pattern);
                }
            });

            put(file, Pair.create(set, patterns));
        }

        /**
         * Checks if {@link IgnoreFile} has changed and rebuilds its cache.
         *
         * @param file to check
         */
        public void hasChanged(@NotNull IgnoreFile file) {
            final Pair<Set<Integer>, Set<Pattern>> recent = get(file);

            final Set<Integer> set = Sets.newHashSet();
            file.acceptChildren(new IgnoreVisitor() {
                @Override
                public void visitEntry(@NotNull IgnoreEntry entry) {
                    set.add(entry.getText().trim().hashCode());
                }
            });

            if (recent == null || !set.equals(recent.getFirst())) {
                add(file);
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
            for (final IgnoreFile ignoreFile : keySet()) {
                final VirtualFile ignoreFileParent = ignoreFile.getVirtualFile().getParent();
                if (!Utils.isUnder(file, ignoreFileParent)) {
                    continue;
                }

                final String path = Utils.getRelativePath(ignoreFileParent, file);
                if (StringUtil.isEmpty(path)) {
                    continue;
                }

                for (Pattern pattern : get(ignoreFile).getSecond()) {
                    if (pattern.matcher(path).matches()) {
                        statuses.put(file, true);
                        return true;
                    }
                }
            }
            statuses.put(file, false);
            return false;
        }

        /**
         * Checks if any of the file parents is ignored.
         *
         * @param file to check
         * @return any of the parents is ignored
         */
        public boolean isParentIgnored(@NotNull VirtualFile file) {
            VirtualFile parent = file.getParent();
            while (parent != null && !parent.equals(baseDir)) {
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
        @Override
        public void clear() {
            super.clear();
            this.statuses.clear();
        }
    }
}
