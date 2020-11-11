// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.file.type

import com.intellij.openapi.fileTypes.LanguageFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage

/**
 * Describes Ignore file type.
 */
open class IgnoreFileType protected constructor(val ignoreLanguage: IgnoreLanguage = IgnoreLanguage.INSTANCE) :
    LanguageFileType(ignoreLanguage) {

    companion object {
        val INSTANCE = IgnoreFileType()
    }

    override fun getName() = ignoreLanguage.id + " file"

    val languageName
        get() = ignoreLanguage.id

    override fun getDescription() = ignoreLanguage.displayName

    override fun getDefaultExtension() = ignoreLanguage.extension

    override fun getIcon() = ignoreLanguage.icon

    override fun hashCode() = ignoreLanguage.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IgnoreFileType

        if (ignoreLanguage != other.ignoreLanguage) return false

        return true
    }
}
