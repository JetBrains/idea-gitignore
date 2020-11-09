// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util.exec.parser

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.util.Utils.resolveUserDir
import java.io.File

/**
 * Parser for the [mobi.hsz.idea.gitignore.util.exec.ExternalExec]#GIT_CONFIG_EXCLUDES_FILE command that returns excludes Git file instance.
 */
class GitExcludesOutputParser : ExecutionOutputParser<VirtualFile>() {

    /**
     * Parses output and returns [VirtualFile] instance of the GitFileType.
     *
     * @param text input data
     * @return excludes ignore file instance
     */
    override fun parseOutput(text: String) = resolveUserDir(text)?.let {
        VfsUtil.findFileByIoFile(File(it), true)
    }
}
