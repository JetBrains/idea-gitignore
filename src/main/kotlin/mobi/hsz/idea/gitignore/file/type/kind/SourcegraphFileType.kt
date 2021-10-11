// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file.type.kind

import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.kind.SourcegraphLanguage

/**
 * Describes Sourcegraph file type.
 */
class SourcegraphFileType : IgnoreFileType(SourcegraphLanguage.INSTANCE), FileTypeIdentifiableByVirtualFile {

    companion object {
        val INSTANCE = SourcegraphFileType()
    }
    override fun isMyFileType(file: VirtualFile) =
        file.name == ignoreLanguage.extension && file.parent.name == ignoreLanguage.vcsDirectory
}
