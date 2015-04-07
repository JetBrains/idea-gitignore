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

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.progress.BackgroundTaskQueue;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Alarm;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.CacheMap;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link IgnoreManager} handles ignore files indexing and status caching.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0
 */
public class IgnoreManager extends AbstractProjectComponent {
    private final CacheMap cache;
    private final PsiManagerImpl psiManager;
    private final VirtualFileManager virtualFileManager;
    private final Alarm alarm = new Alarm();
    private final BackgroundTaskQueue queue;
    private final IgnoreSettings settings;
    private boolean working;

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
         * Adds {@link IgnoreFile} to the {@link CacheMap}.
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
         * Removes {@link IgnoreFile} from the {@link CacheMap}.
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

    private final PsiTreeChangeListener psiTreeChangeListener = new PsiTreeChangeAdapter() {
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
        this.cache = new CacheMap(project);
        this.psiManager = (PsiManagerImpl) PsiManager.getInstance(project);
        this.virtualFileManager = VirtualFileManager.getInstance();
        this.queue = new BackgroundTaskQueue(project, IgnoreBundle.message("cache.indexing"));
        this.settings = IgnoreSettings.getInstance();

        this.settings.addListener(new IgnoreSettings.Listener() {
            @Override
            public void onChange(@NotNull String key, Object value) {
                if ("ignoredFileStatus".equals(key) || "outerIgnoreRules".equals(key)) {
                    if ((Boolean) value) {
                        enable();
                    } else {
                        disable();
                    }
                }
            }
        });
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
        return isEnabled() && cache.isFileIgnored(file);
    }

    /**
     * Checks if ignored files watching is enabled.
     *
     * @return enabled
     */
    private boolean isEnabled() {
        boolean enabled = settings.isIgnoredFileStatus();
        if (enabled && !working) {
            enable();
        } else if (!enabled && working) {
            disable();
        }
        return enabled;
    }

    /**
     * Invoked when the project corresponding to this component instance is opened.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component instance (for example for default project).
     */
    @Override
    public void projectOpened() {
        if (isEnabled() && !working) {
            enable();
        }
    }

    /**
     * Enable manager.
     */
    private void enable() {
        this.virtualFileManager.addVirtualFileListener(virtualFileListener);
        this.psiManager.addPsiTreeChangeListener(psiTreeChangeListener);
        this.working = true;

        alarm.cancelAllRequests();
        alarm.addRequest(new Runnable() {
            @Override
            public void run() {
                if (((FileManagerImpl) psiManager.getFileManager()).isInitialized()) {
                    alarm.cancelAllRequests();
                    queue.clear();

                    // Search for Ignore files in the project
                    final GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);
                    for (final IgnoreFileType type : IgnoreBundle.FILE_TYPES) {
                        for (VirtualFile virtualFile : FileTypeIndex.getFiles(type, scope)) {
                            addTaskFor(getIgnoreFile(virtualFile));
                        }
                    }

                    // Search for outer files
                    if (settings.isOuterIgnoreRules()) {
                        for (IgnoreFileType fileType : IgnoreBundle.FILE_TYPES) {
                            VirtualFile outerFile = fileType.getIgnoreLanguage().getOuterFile();
                            if (outerFile != null) {
                                addTaskFor((IgnoreFile) psiManager.findFile(outerFile));
                            }
                        }
                    }
                }
            }

            /**
             * Adds {@link IgnoreFile} to the cache processor queue.
             *
             * @param file to cache
             */
            private void addTaskFor(@Nullable final IgnoreFile file) {
                if (file == null) {
                    return;
                }
                queue.run(new Task.Backgroundable(myProject, IgnoreBundle.message("cache.indexing")) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        String path = Utils.getRelativePath(myProject.getBaseDir(), file.getVirtualFile());
                        indicator.setText(path);
                        cache.add(file);
                    }
                });
            }
        }, 200);
    }

    /**
     * Invoked when the project corresponding to this component instance is closed.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component instance (for example for default project).
     */
    @Override
    public void projectClosed() {
        disable();
    }

    /**
     * Disable manager.
     */
    private void disable() {
        this.virtualFileManager.removeVirtualFileListener(virtualFileListener);
        this.psiManager.removePsiTreeChangeListener(psiTreeChangeListener);
        this.cache.clear();
        this.working = false;
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
}
