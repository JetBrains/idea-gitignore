// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.annotations.NonNls

/**
 * [Properties] util class that holds project specified settings using [PropertiesComponent].
 */
object Properties {

    /** Ignore missing gitignore property key.  */
    @NonNls
    private val IGNORE_MISSING_GITIGNORE = "ignore_missing_gitignore"

    /** Add unversioned files property key.  */
    @NonNls
    private val ADD_UNVERSIONED_FILES = "add_unversioned_files"

    /** Dismissed ignored editing notification key.  */
    @NonNls
    private val DISMISSED_IGNORED_EDITING_NOTIFICATION = "add_unversioned_files"

    /**
     * Checks value of [.IGNORE_MISSING_GITIGNORE] key in [PropertiesComponent].
     *
     * @param project current project
     * @return [.IGNORE_MISSING_GITIGNORE] value
     */
    fun isIgnoreMissingGitignore(project: Project) = properties(project).getBoolean(IGNORE_MISSING_GITIGNORE, false)

    /**
     * Sets value of [.IGNORE_MISSING_GITIGNORE] key in [PropertiesComponent] to `true`.
     *
     * @param project current project
     */
    fun setIgnoreMissingGitignore(project: Project) {
        properties(project).setValue(IGNORE_MISSING_GITIGNORE, true)
    }

    /**
     * Checks value of [.ADD_UNVERSIONED_FILES] key in [PropertiesComponent].
     *
     * @param project current project
     * @return [.ADD_UNVERSIONED_FILES] value
     */
    fun isAddUnversionedFiles(project: Project) = properties(project).getBoolean(ADD_UNVERSIONED_FILES, false)

    /**
     * Sets value of [.ADD_UNVERSIONED_FILES] key in [PropertiesComponent] to `true`.
     *
     * @param project current project
     */
    fun setAddUnversionedFiles(project: Project) {
        properties(project).setValue(ADD_UNVERSIONED_FILES, true)
    }

    /**
     * Checks if user already dismissed notification about editing ignored file.
     *
     * @param project current project
     * @param file    current file
     * @return notification was dismissed
     */
    fun isDismissedIgnoredEditingNotification(project: Project, file: VirtualFile) =
        properties(project).getValues(DISMISSED_IGNORED_EDITING_NOTIFICATION)?.contains(file.canonicalPath) ?: false

    /**
     * Stores information about dismissed notification about editing ignored file.
     *
     * @param project current project
     * @param file    current file
     */
    fun setDismissedIgnoredEditingNotification(project: Project, file: VirtualFile) {
        val props = properties(project)
        val values = props.getValues(DISMISSED_IGNORED_EDITING_NOTIFICATION)
        val set = ContainerUtil.newHashSet(*values ?: arrayOfNulls(0))
        set.add(file.canonicalPath)
        props.setValues(DISMISSED_IGNORED_EDITING_NOTIFICATION, set.toTypedArray())
    }

    /**
     * Shorthand for [PropertiesComponent.getInstance] method.
     *
     * @param project current project
     * @return component instance
     */
    private fun properties(project: Project) = PropertiesComponent.getInstance(project)
}
