// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang

import com.intellij.lang.InjectableLanguage
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import mobi.hsz.idea.gitignore.IgnoreBundle.Syntax
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.outer.OuterIgnoreLoaderComponent.OuterFileFetcher
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.ExpiringMap
import mobi.hsz.idea.gitignore.util.Icons
import org.apache.commons.lang.builder.HashCodeBuilder
import org.jetbrains.annotations.NonNls
import java.util.HashSet
import javax.swing.Icon

/**
 * Gitignore [Language] definition.
 *
 * @author Jakub Chrzanowski <jakub></jakub>@hsz.mobi>
 * @since 0.8
 */
open class IgnoreLanguage protected constructor(
    name: String = "Ignore",
    val extension: String = "ignore",
    val vcsDirectory: String? = null,
    val icon: Icon? = Icons.IGNORE,
    private val outerFileFetchers: Array<OuterFileFetcher?> = arrayOfNulls(0)
) : Language(name), InjectableLanguage {

    private val languagesSettings = IgnoreSettings.getInstance().languagesSettings

    @JvmField
    protected val outerFiles = ExpiringMap<Int, Set<VirtualFile>>(5000)

    companion object {
        /** The [IgnoreLanguage] instance.  */
        @JvmField
        val INSTANCE = IgnoreLanguage()

        /** The dot.  */
        @NonNls
        private val DOT = "."
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
     * Defines if current [IgnoreLanguage] supports outer ignore files.
     *
     * @return supports outer ignore files
     */
    open val isOuterFileSupported
        get() = false

    /**
     * Returns outer files for the current language.
     *
     * @param project current project
     * @return outer files
     */
    fun getOuterFiles(project: Project) = getOuterFiles(project, false)

    /**
     * Returns outer files for the current language.
     *
     * @param project current project
     * @return outer files
     */
    open fun getOuterFiles(project: Project, dumb: Boolean): Set<VirtualFile> {
        val key = HashCodeBuilder().append(project).append(fileType).toHashCode()
        if (outerFiles[key] == null) {
            outerFiles[key] = outerFileFetchers.map {
                it!!.fetch(project)
            }.flatten().toSet()
        }
        return outerFiles.getOrElse(key, HashSet())
    }

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

    /**
     * Returns fixed directory for the given [IgnoreLanguage].
     *
     * @param project current project
     * @return fixed directory
     */
    open fun getFixedDirectory(project: Project): VirtualFile? = null
}
