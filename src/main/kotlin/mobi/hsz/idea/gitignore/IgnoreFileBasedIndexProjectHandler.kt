// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.roots.ContentIterator
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.IndexableFileSet
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.indexing.ExternalIndexableSetContributor

/**
 * Project component that registers [IndexableFileSet] that counts into indexing files located outside of the project.
 */
class IgnoreFileBasedIndexProjectHandler(val project: Project, private val projectManager: ProjectManager, val index: FileBasedIndex) :
    IndexableFileSet, ProjectComponent {

    init {
        StartupManager.getInstance(project).registerPreStartupActivity {
            index.registerIndexableSet(this@IgnoreFileBasedIndexProjectHandler, project)
            project.messageBus.syncPublisher(IgnoreManager.RefreshStatusesListener.REFRESH_STATUSES).refresh()
        }
    }

    /** Project listener to remove [IndexableFileSet] from the indexable sets.  */
    private val projectListener = object : ProjectManagerListener {
        override fun projectClosing(project: Project) {
            index.removeIndexableSet(this@IgnoreFileBasedIndexProjectHandler)
        }
    }

    override fun initComponent() {
        projectManager.addProjectManagerListener(project, projectListener)
    }

    override fun disposeComponent() {
        projectManager.removeProjectManagerListener(project, projectListener)
    }

    override fun isInSet(file: VirtualFile) = file.fileType is IgnoreFileType &&
        ExternalIndexableSetContributor.getAdditionalFiles(project).contains(file)

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
