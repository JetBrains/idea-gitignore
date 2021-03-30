// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.ModuleListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener
import mobi.hsz.idea.gitignore.IgnoreManager.RefreshStatusesListener

/**
 * Wrapper for common listeners.
 */
class CommonRunnableListeners(private val task: Runnable) : RefreshStatusesListener, ModuleRootListener, ModuleListener {

    override fun refresh() = task.run()

    override fun beforeRootsChange(event: ModuleRootEvent) = Unit

    override fun rootsChanged(event: ModuleRootEvent) = task.run()

    override fun moduleAdded(project: Project, module: Module) = task.run()

    override fun beforeModuleRemoved(project: Project, module: Module) = Unit

    override fun moduleRemoved(project: Project, module: Module) = task.run()
}
