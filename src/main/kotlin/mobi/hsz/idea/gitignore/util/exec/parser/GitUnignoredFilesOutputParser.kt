// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util.exec.parser

import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.annotations.NonNls

/**
 * Parser for the [mobi.hsz.idea.gitignore.util.exec.ExternalExec.GIT_UNIGNORED_FILES] command that returns unignored files entries list.
 */
class GitUnignoredFilesOutputParser : ExecutionOutputParser<String>() {

    companion object {
        /** Prefix to remove from the command's result. */
        @NonNls
        private val GIT_UNIGNORED_FILES_PREFIX = "Would remove"
    }

    /**
     * Parses single entries and removes git output prefixes.
     *
     * @param text input data
     * @return single unignored entry
     */
    override fun parseOutput(text: String): String? = StringUtil.trim(StringUtil.trimStart(text, GIT_UNIGNORED_FILES_PREFIX))
}
