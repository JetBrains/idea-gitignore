// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType
import mobi.hsz.idea.gitignore.indexing.IgnoreFilesIndex
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.outer.OuterFileFetcher
import mobi.hsz.idea.gitignore.util.Icons
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.ExternalExec
import org.apache.commons.lang.builder.HashCodeBuilder
import java.util.HashSet

/**
 * Gitignore [IgnoreLanguage] definition.
 */
class GitLanguage private constructor() : IgnoreLanguage(
    "Git",
    "gitignore",
    ".git",
    Icons.GIT,
    arrayOf( // Outer file fetched from the `git config core.excludesfile`.
        OuterFileFetcher { listOf(ExternalExec.gitExcludesFile) },
        OuterFileFetcher { listOf(ExternalExec.GIT_USER_IGNORE) }
    )
) {

    private var fetched = false

    companion object {
        val INSTANCE = GitLanguage()
    }

    override val fileType
        get() = GitFileType.INSTANCE

    override val isOuterFileSupported
        get() = true

    override fun getOuterFiles(project: Project, dumb: Boolean): Set<VirtualFile> {
        val key = HashCodeBuilder().append(project).append(fileType).toHashCode()
        if (dumb || fetched && outerFiles[key] != null) {
            return super.getOuterFiles(project, true)
        }
        fetched = true

        super.getOuterFiles(project, false).toMutableList().addAll(
            IgnoreFilesIndex.getFiles(project, GitExcludeFileType.INSTANCE).filter { Utils.isInProject(it, project) }
        )
        return outerFiles.getOrElse(key, HashSet())
    }
}
