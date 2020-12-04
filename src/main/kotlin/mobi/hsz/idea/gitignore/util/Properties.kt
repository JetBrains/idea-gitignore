// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls

/**
 * [Properties] util class that holds project specified settings using [PropertiesComponent].
 */
object Properties {

    /** Ignore missing gitignore property key.  */
    @NonNls
    private val IGNORE_MISSING_GITIGNORE = "ignore_missing_gitignore"

    /** Dismissed ignored editing notification key.  */
    @NonNls
    private val DISMISSED_IGNORED_EDITING_NOTIFICATION = "add_unversioned_files"

    /**
     * Checks value of [.IGNORE_MISSING_GITIGNORE] key in [PropertiesComponent].
     *
     * @param project current project
     * @return [.IGNORE_MISSING_GITIGNORE] value
     */
    fun isIgnoreMissingGitignore(project: Project) = project.service<PropertiesComponent>()
        .getBoolean(IGNORE_MISSING_GITIGNORE, false)

    /**
     * Sets value of [.IGNORE_MISSING_GITIGNORE] key in [PropertiesComponent] to `true`.
     *
     * @param project current project
     */
    fun setIgnoreMissingGitignore(project: Project) = project.service<PropertiesComponent>()
        .setValue(IGNORE_MISSING_GITIGNORE, true)

    /**
     * Checks if user already dismissed notification about editing ignored file.
     *
     * @param project current project
     * @param file    current file
     * @return notification was dismissed
     */
    fun isDismissedIgnoredEditingNotification(project: Project, file: VirtualFile) = project.service<PropertiesComponent>()
        .getValues(DISMISSED_IGNORED_EDITING_NOTIFICATION)?.contains(file.canonicalPath) ?: false

    /**
     * Stores information about dismissed notification about editing ignored file.
     *
     * @param project current project
     * @param file    current file
     */
    fun setDismissedIgnoredEditingNotification(project: Project, file: VirtualFile) = project.service<PropertiesComponent>().let {
        it.getValues(DISMISSED_IGNORED_EDITING_NOTIFICATION).toHashSet().apply {
            add(file.canonicalPath)
            it.setValues(DISMISSED_IGNORED_EDITING_NOTIFICATION, toTypedArray())
        }
    }
}
