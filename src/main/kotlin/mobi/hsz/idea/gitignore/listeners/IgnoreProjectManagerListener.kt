// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.IndexableFileSet
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor

/**
 * Project listener that registers [IndexableFileSet] that counts into indexing files located outside of the project.
 */
class IgnoreProjectManagerListener : IndexableFileSet, ProjectManagerListener {

    private lateinit var project: Project
    private val fileBasedIndex = FileBasedIndex.getInstance()

    override fun projectOpened(project: Project) {
        fileBasedIndex.registerIndexableSet(this, project)
        project.messageBus.syncPublisher(IgnoreManager.RefreshStatusesListener.REFRESH_STATUSES).refresh()
        project.service<IgnoreManager>().projectOpened()

        this.project = project
    }

    override fun projectClosing(project: Project) {
        fileBasedIndex.removeIndexableSet(this)
        // TODO: fix that!
        project.service<IgnoreManager>().projectClosed()
    }

    override fun isInSet(file: VirtualFile) =
        file.fileType is IgnoreFileType && ExternalIndexableSetContributor.getAdditionalFiles(project).contains(file)

    override fun iterateIndexableFilesIn(file: VirtualFile, iterator: ContentIterator) {
        VfsUtilCore.visitChildrenRecursively(
            file,
            object : VirtualFileVisitor<Any?>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!isInSet(file)) {
                        return false
                    }
                    if (!file.isDirectory) {
                        iterator.processFile(file)
                    }
                    return true
                }
            }
        )
    }
}
