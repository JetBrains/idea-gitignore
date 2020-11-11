// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util.exec.parser

/**
 * Simple parser that returns trimmed input.
 */
class SimpleOutputParser : ExecutionOutputParser<String>() {

    override fun parseOutput(text: String) = text.trim { it <= ' ' }
}
