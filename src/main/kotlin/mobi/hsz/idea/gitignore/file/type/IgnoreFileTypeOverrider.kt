package mobi.hsz.idea.gitignore.file.type

import com.intellij.openapi.fileTypes.impl.FileTypeOverrider
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.kind.SourcegraphFileType

@Suppress("UnstableApiUsage")
class IgnoreFileTypeOverrider : FileTypeOverrider {

    override fun getOverriddenFileType(file: VirtualFile) = when {
        file.name == "ignore" && file.parent.name == ".sourcegraph" -> SourcegraphFileType.INSTANCE
        else -> null
    }
}
