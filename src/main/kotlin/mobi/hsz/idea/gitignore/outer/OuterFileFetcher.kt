package mobi.hsz.idea.gitignore.outer

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/** Outer file fetcher event interface. */
fun interface OuterFileFetcher {

    fun fetch(project: Project): Collection<VirtualFile?>
}
