// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.text.nullize
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.util.Notify
import mobi.hsz.idea.gitignore.util.Utils
import org.jetbrains.annotations.PropertyKey

/**
 * Action that adds currently selected [VirtualFile] to the specified Ignore [VirtualFile].
 * Action is added to the IDE context menus not directly but with [IgnoreFileGroupAction] action.
 */
open class IgnoreFileAction(
    private val ignoreFile: VirtualFile? = null,
    private val fileType: IgnoreFileType? = getFileType(ignoreFile),
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME) textKey: String = "action.addToIgnore",
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME) descriptionKey: String = "action.addToIgnore.description"
) : DumbAwareAction(
    IgnoreBundle.message(textKey, fileType?.ignoreLanguage?.filename),
    IgnoreBundle.message(descriptionKey, fileType?.ignoreLanguage?.filename),
    fileType?.icon
) {

    companion object {
        /**
         * Returns [IgnoreFileType] basing on the [VirtualFile] file.
         *
         * @param virtualFile current file
         * @return file type
         */
        fun getFileType(virtualFile: VirtualFile?) = virtualFile?.run {
            fileType.takeIf { it is IgnoreFileType } as? IgnoreFileType
        }
    }

    /**
     * Adds currently selected [VirtualFile] to the [.ignoreFile].
     * If [.ignoreFile] is null, default project's Gitignore file will be used.
     * Files that cannot be covered with Gitignore file produces error notification.
     * When action is performed, Gitignore file is opened with additional content added using [AppendFileCommandAction].
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val files = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)

        ignoreFile?.let {
            Utils.getPsiFile(project, it)
        } ?: fileType?.let {
            getIgnoreFile(project, it, null, true)
        }?.let { ignore ->
            val paths = mutableSetOf<String>()

            files.forEach { file ->
                val path = getPath(ignore.virtualFile.parent, file)
                if (path.isEmpty()) {
                    Utils.getModuleRootForFile(file, project)?.let { baseDir ->
                        Notify.show(
                            project,
                            IgnoreBundle.message("action.ignoreFile.addError", Utils.getRelativePath(baseDir, file)),
                            IgnoreBundle.message("action.ignoreFile.addError.to", Utils.getRelativePath(baseDir, ignore.virtualFile)),
                            NotificationType.ERROR
                        )
                    }
                } else {
                    paths.add(path)
                }
            }

            Utils.openFile(project, ignore)

            try {
                AppendFileCommandAction(project, ignore, paths).execute()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (e.project == null || files == null) {
            e.presentation.isVisible = false
        }
    }

    /**
     * Gets the file's path relative to the specified root directory.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    protected open fun getPath(root: VirtualFile, file: VirtualFile) =
        StringUtil.notNullize(Utils.getRelativePath(root, file))
            .run { StringUtil.escapeChar(this, '[') }
            .run { StringUtil.escapeChar(this, ']') }
            .run { StringUtil.trimLeading(this, '/') }
            .nullize()?.run { "/$this" } ?: ""

    /**
     * Gets Ignore file for given [Project] and root [PsiDirectory].
     * If file is missing - creates new one.
     *
     * @param project         current project
     * @param fileType        current ignore file type
     * @param createIfMissing create new file if missing
     * @return Ignore file
     */
    private fun getIgnoreFile(project: Project, fileType: IgnoreFileType, psiDirectory: PsiDirectory?, createIfMissing: Boolean): PsiFile? {
        val projectDir = project.guessProjectDir() ?: return null
        val directory = psiDirectory ?: PsiManager.getInstance(project).findDirectory(projectDir)

        val filename = fileType.ignoreLanguage.filename
        var file = directory!!.findFile(filename)
        val virtualFile = file?.virtualFile ?: directory.virtualFile.findChild(filename)

        if (file == null && virtualFile == null && createIfMissing) {
            try {
                file = CreateFileCommandAction(project, directory, fileType).execute()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }

        return file
    }
}
