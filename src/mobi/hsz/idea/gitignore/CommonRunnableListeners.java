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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Wrapper for common listeners.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.2.0
 */
public class CommonRunnableListeners implements
        IgnoreManager.RefreshStatusesListener, ModuleRootListener, ModuleListener {
    /** Task to run. */
    @NotNull
    private final Runnable task;

    /**
     * Constructor.
     *
     * @param task to run by all listeners
     */
    public CommonRunnableListeners(@NotNull Runnable task) {
        this.task = task;
    }

    /**
     * {@link IgnoreManager.RefreshStatusesListener} event.
     */
    @Override
    public void refresh() {
        task.run();
    }

    /**
     * {@link ModuleRootListener} event (ignored).
     */
    @Override
    public void beforeRootsChange(@NotNull ModuleRootEvent event) {
    }

    /**
     * {@link ModuleRootListener} event.
     */
    @Override
    public void rootsChanged(@NotNull ModuleRootEvent event) {
        task.run();
    }

    /**
     * {@link ModuleListener} event.
     */
    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        task.run();
    }

    /**
     * {@link ModuleListener} event (ignored).
     */
    @Override
    public void beforeModuleRemoved(@NotNull Project project, @NotNull Module module) {
    }

    /**
     * {@link ModuleListener} event.
     */
    @Override
    public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        task.run();
    }

    /**
     * {@link ModuleListener} event.
     */
    @Override
    public void modulesRenamed(@NotNull Project project, @NotNull List<Module> modules,
                               @NotNull Function<Module, String> oldNameProvider) {
        task.run();
    }
}
