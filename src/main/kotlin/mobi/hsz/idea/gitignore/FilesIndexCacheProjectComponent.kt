// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.rd.util.concurrentMapOf
import gnu.trove.THashSet
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.MatcherUtil
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Cache that retrieves matching files using given [Pattern].
 * It uses [VirtualFileListener] to handle changes in the files tree and clear cached entries for the specific pattern parts.
 */
class FilesIndexCacheProjectComponent(project: Project) : ProjectComponent, BulkFileListener, Disposable {

    private val cacheMap = concurrentMapOf<String, Collection<VirtualFile>>()
    private val projectFileIndex = ProjectRootManager.getInstance(project).fileIndex
    private val messageBus = ApplicationManager.getApplication().messageBus.connect(this)

    companion object {
        /**
         * Returns [FilesIndexCacheProjectComponent] service instance.
         *
         * @param project current project
         * @return [instance][FilesIndexCacheProjectComponent]
         */
        fun getInstance(project: Project): FilesIndexCacheProjectComponent =
            project.getComponent(FilesIndexCacheProjectComponent::class.java)
    }

    init {
        messageBus.subscribe(VirtualFileManager.VFS_CHANGES, this)
    }

    /** Unregisters [.virtualFileListener] when project is closed.  */
    override fun projectClosed() {
        cacheMap.clear()
    }

    /**
     * Finds [VirtualFile] instances for the specific [Pattern] and caches them.
     *
     * @param project current project
     * @param pattern to handle
     * @return matched files list
     */
    fun getFilesForPattern(project: Project, pattern: Pattern): Collection<VirtualFile> {
        val scope = GlobalSearchScope.projectScope(project)
        val parts = MatcherUtil.getParts(pattern)

        if (parts.isNotEmpty()) {
            val key = StringUtil.join(parts, Constants.DOLLAR)
            if (cacheMap[key] == null) {
                val files = THashSet<VirtualFile>(1000)
                projectFileIndex.iterateContent { fileOrDir: VirtualFile ->
                    val name = fileOrDir.name
                    if (MatcherUtil.matchAnyPart(parts, name)) {
                        FilenameIndex.getVirtualFilesByName(project, name, scope).forEach { file ->
                            if (file.isValid && MatcherUtil.matchAllParts(parts, file.path)) {
                                files.add(file)
                            }
                        }
                    }
                    true
                }
                cacheMap[key] = files
            }
            return cacheMap[key]!!
        }
        return ArrayList()
    }

    /**
     * Returns component's name.
     *
     * @return component's name
     */
    override fun getComponentName() = "FilesIndexCacheProjectComponent"

    override fun dispose() {
        messageBus.disconnect()
    }

    override fun before(events: MutableList<out VFileEvent>) {
        events.forEach {
            if (it !is VFilePropertyChangeEvent || it.propertyName == "name") {
                for (key in cacheMap.keys) {
                    val parts = StringUtil.split(key, Constants.DOLLAR)
                    if (MatcherUtil.matchAnyPart(parts.toTypedArray(), it.file?.path)) {
                        cacheMap.remove(key)
                    }
                }
            }
        }
    }
}
