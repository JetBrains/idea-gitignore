// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.file.type.kind.GitExcludeFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.outer.OuterFileFetcher
import mobi.hsz.idea.gitignore.util.Icons
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.ExternalExec
import org.jetbrains.annotations.NonNls

/**
 * Gitignore Exclude [IgnoreLanguage] definition.
 */
class GitExcludeLanguage private constructor() :
    IgnoreLanguage(
        "Git exclude",
        "exclude",
        ".git",
        Icons.GIT,
        arrayOf( // `exclude` files located in .git directory
            object : OuterFileFetcher {
                @NonNls
                val EXCLUDE = "info/exclude"
                override fun fetch(project: Project): Collection<VirtualFile> {

                    return Utils.guessProjectDir(project)?.findChild(".git")?.run {
                        processExcludes(this)
                    } ?: emptyList()
                }

                /**
                 * Recursively finds exclude files in given root directory.
                 *
                 * @param root  current root
                 * @param files collection of [VirtualFile]
                 * @return exclude files collection
                 */
                private fun processExcludes(
                    root: VirtualFile,
                    files: MutableCollection<VirtualFile> = mutableListOf()
                ): Collection<VirtualFile> = root.run {
                    findFileByRelativePath(EXCLUDE)?.let(files::add)
                    findChild("modules")?.let { modules ->
                        VfsUtil.visitChildrenRecursively(
                            modules,
                            object : VirtualFileVisitor<VirtualFile>() {
                                override fun visitFile(dir: VirtualFile) =
                                    dir.findChild("index")?.let {
                                        processExcludes(it, files)
                                        false
                                    } ?: dir.isDirectory
                            }
                        )
                    }
                    files
                }
            },
            OuterFileFetcher { ContainerUtil.newArrayList(ExternalExec.GIT_USER_IGNORE) }
        )
    ) {

    companion object {
        val INSTANCE = GitExcludeLanguage()
    }

    override val fileType
        get() = GitExcludeFileType.INSTANCE

    override val filename
        get() = super.extension

    override val isOuterFileSupported
        get() = true

    override fun getFixedDirectory(project: Project) =
        Utils.guessProjectDir(project)?.findFileByRelativePath("$vcsDirectory/info")
}
