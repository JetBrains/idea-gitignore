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

    /**
     * Returns the name of the file type. The name must be unique among all file types registered in the system.
     *
     * @return The file type name.
     */
    override fun getName() = ignoreLanguage.id + " file"

    /**
     * Returns the name of the language.
     *
     * @return The language name.
     */
    val languageName
        get() = ignoreLanguage.id

    /**
     * Returns the user-readable description of the file type.
     *
     * @return The file type description.
     */
    override fun getDescription() = ignoreLanguage.displayName

    /**
     * Returns the default extension for files of the type.
     *
     * @return The extension, not including the leading '.'.
     */
    override fun getDefaultExtension() = ignoreLanguage.extension

    /**
     * Returns the icon used for showing files of the type.
     *
     * @return The icon instance, or null if no icon should be shown.
     */
    override fun getIcon() = ignoreLanguage.icon

    /**
     * Returns hashCode of the current [IgnoreLanguage].
     *
     * @return hashCode
     */
    override fun hashCode() = ignoreLanguage.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IgnoreFileType

        if (ignoreLanguage != other.ignoreLanguage) return false

        return true
    }
}
