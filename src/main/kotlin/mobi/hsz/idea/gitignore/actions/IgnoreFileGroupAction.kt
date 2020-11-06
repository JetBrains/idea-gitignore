// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.lang.kind.MercurialLanguage
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.ExternalFileException
import mobi.hsz.idea.gitignore.util.Utils
import org.jetbrains.annotations.PropertyKey

/**
 * Group action that ignores specified file or directory.
 * [ActionGroup] expands single action into a more child options to allow user specify
 * the IgnoreFile that will be used for file's path storage.
 */
open class IgnoreFileGroupAction @JvmOverloads constructor(
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME) textKey: String? = "action.addToIgnore.group",
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME) descriptionKey: String? = "action.addToIgnore.group.description",
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME) textSingleKey: String = "action.addToIgnore.group.noPopup"
) : ActionGroup() {
    /** List of suitable Gitignore [VirtualFile]s that can be presented in an IgnoreFile action. */
    private val files = mutableMapOf<IgnoreFileType, List<VirtualFile>>()

    /** Action presentation's text for single element. */
    @PropertyKey(resourceBundle = IgnoreBundle.BUNDLE_NAME)
    private val presentationTextSingleKey: String

    /** [Project]'s base directory. */
    private var baseDir: VirtualFile? = null

    companion object {
        /** Maximum filename length for the action name.*/
        private const val FILENAME_MAX_LENGTH = 30
    }

    init {
        templatePresentation.apply {
            text = IgnoreBundle.message(textKey)
            description = IgnoreBundle.message(descriptionKey)
        }
        presentationTextSingleKey = textSingleKey
    }

    /**
     * Presents a list of suitable Gitignore files that can cover currently selected [VirtualFile].
     * Shows a subgroup with available files or one option if only one Gitignore file is available.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.getData(CommonDataKeys.PROJECT)
        val presentation = e.presentation

        files.clear()
        if (project != null && file != null) {
            try {
                presentation.isVisible = true
                baseDir = Utils.getModuleRootForFile(file, project)
                IgnoreBundle.LANGUAGES
                    // skip already bundled languages for ignore action
                    .filterNot { this !is UnignoreFileGroupAction && (it is GitLanguage || it is MercurialLanguage) }
                    .map { it.fileType }
                    .forEach { files[it] = Utils.getSuitableIgnoreFiles(project, it, file).apply { reverse() } }
            } catch (e: ExternalFileException) {
                presentation.isVisible = false
            }
        }

        isPopup = countFiles() > 1
    }

    /**
     * Creates subactions bound to the specified Gitignore [VirtualFile]s using [IgnoreFileAction].
     *
     * @param e action event
     * @return actions list
     */
    override fun getChildren(e: AnActionEvent?): Array<AnAction> = when {
        countFiles() == 0 || baseDir == null -> emptyArray()
        else -> mutableListOf<AnAction>().apply {
            val project = getEventProject(e)
            files.forEach { (key, value) ->
                value.forEachIndexed { index, file ->
                    add(
                        index,
                        createAction(file).apply {
                            val directory = project?.let { Utils.getModuleRootForFile(file, project) }
                            var name = directory?.let { Utils.getRelativePath(directory, file) } ?: file.name

                            if (StringUtil.isNotEmpty(name)) {
                                name = StringUtil.shortenPathWithEllipsis(name, FILENAME_MAX_LENGTH)
                            }
                            if (countFiles() == 1) {
                                name = IgnoreBundle.message(presentationTextSingleKey, name)
                            }

                            templatePresentation.apply {
                                icon = key.icon
                                text = name
                            }
                        }
                    )
                }
            }
        }.toTypedArray()
    }

    /**
     * Creates new [IgnoreFileAction] action instance.
     *
     * @param file current file
     * @return action instance
     */
    protected open fun createAction(file: VirtualFile) = IgnoreFileAction(file)

    /**
     * Counts items in [.files] map.
     *
     * @return files amount
     */
    private fun countFiles() = files.values.sumBy { it.size }
}
