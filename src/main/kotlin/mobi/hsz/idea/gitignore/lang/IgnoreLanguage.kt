// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.InjectableLanguage
import com.intellij.lang.Language
import com.intellij.openapi.components.service
import com.intellij.psi.FileViewProvider
import mobi.hsz.idea.gitignore.IgnoreBundle.Syntax
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Icons
import javax.swing.Icon

/**
 * Gitignore [Language] definition.
 */
open class IgnoreLanguage protected constructor(
    name: String,
    val extension: String,
    val vcsDirectory: String? = null,
    val icon: Icon? = Icons.IGNORE
) : Language(name), InjectableLanguage {

    constructor() : this("Ignore", "ignore")

    private val languagesSettings
        get() = service<IgnoreSettings>().languagesSettings

    companion object {
        val INSTANCE = IgnoreLanguage()

        private const val DOT = "."
    }

    open val filename
        get() = DOT + extension

    override fun getDisplayName() = "$filename ($id)"

    open val fileType
        get() = IgnoreFileType.INSTANCE

    fun createFile(viewProvider: FileViewProvider) = IgnoreFile(viewProvider, fileType)

    open val isSyntaxSupported
        get() = false

    open val defaultSyntax
        get() = Syntax.GLOB

    /**
     * Checks is language is enabled or with [IgnoreSettings].
     *
     * @return language is enabled
     */
    val isEnabled
        get() = languagesSettings[this]?.let {
            it[IgnoreSettings.IgnoreLanguagesSettings.KEY.ENABLE].toString().toBoolean()
        } ?: false

    /**
     * Checks if creating new file for given language is allowed with the settings.
     *
     * @return new file action is allowed
     */
    val isNewAllowed
        get() = languagesSettings[this]?.let {
            it[IgnoreSettings.IgnoreLanguagesSettings.KEY.NEW_FILE].toString().toBoolean()
        } ?: false

    /**
     * Language is related to the VCS.
     *
     * @return is VCS
     */
    open val isVCS
        get() = true
}
