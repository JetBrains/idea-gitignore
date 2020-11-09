// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file.type.kind

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.ExternalExec

/**
 * Describes Git exclude file type.
 */
class GitExcludeFileType : IgnoreFileType() {

    companion object {
        val INSTANCE = GitExcludeFileType()

        fun getWorkingDirectory(project: Project, outerFile: VirtualFile): VirtualFile? {
            Utils.guessProjectDir(project)?.let { baseDir ->
                if (outerFile == ExternalExec.GIT_USER_IGNORE) {
                    return baseDir
                }
                baseDir.findFileByRelativePath(".git/info")?.let {
                    if (Utils.isUnder(outerFile, it)) {
                        return baseDir
                    }
                }
                baseDir.findFileByRelativePath(".git/modules")?.let {
                    if (Utils.isUnder(outerFile, it)) {
                        Utils.getRelativePath(it, outerFile.parent.parent)?.let { path ->
                            return baseDir.findFileByRelativePath(path)
                        }
                    }
                }
            }
            return null
        }
    }
}
