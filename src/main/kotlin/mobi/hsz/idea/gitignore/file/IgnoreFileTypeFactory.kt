// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file

import com.intellij.openapi.fileTypes.ExactFileNameMatcher
import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.kind.GitExcludeLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.lang.kind.MercurialLanguage

/**
 * Class that assigns file types with languages.
 */
class IgnoreFileTypeFactory : FileTypeFactory() {

    /**
     * Assigns file types with languages.
     *
     * @param consumer file types consumer
     */
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consume(consumer, IgnoreFileType.INSTANCE)
        IgnoreBundle.LANGUAGES
            .asSequence()
            .filter { it !is GitLanguage && it !is GitExcludeLanguage && it !is MercurialLanguage }
            .forEach { consume(consumer, it.fileType) }
    }

    /**
     * Shorthand for consuming ignore file types.
     *
     * @param consumer file types consumer
     * @param fileType file type to consume
     */
    private fun consume(consumer: FileTypeConsumer, fileType: IgnoreFileType) {
        consumer.consume(fileType, ExactFileNameMatcher(fileType.ignoreLanguage.filename))
        consumer.consume(fileType, fileType.ignoreLanguage.extension)
    }
}
