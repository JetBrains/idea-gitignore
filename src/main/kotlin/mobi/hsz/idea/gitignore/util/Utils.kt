// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreBundle.obtainLanguage
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

/**
 * [Utils] class that contains various methods.
 */
object Utils {

    /**
     * Gets relative path of given @{link VirtualFile} and root directory.
     *
     * @param directory root directory
     * @param file      file to get it's path
     * @return relative path
     */
    fun getRelativePath(directory: VirtualFile, file: VirtualFile) =
        VfsUtilCore.getRelativePath(file, directory, '/')?.let {
            it + ('/'.takeIf { file.isDirectory } ?: "")
        }

    /**
     * Finds [PsiFile] for the given [VirtualFile] instance. If file is outside current project,
     * it's required to create new [PsiFile] manually.
     *
     * @param project     current project
     * @param virtualFile to handle
     * @return [PsiFile] instance
     */
    fun getPsiFile(project: Project, virtualFile: VirtualFile) =
        PsiManager.getInstance(project).findFile(virtualFile) ?: run {
            PsiManager.getInstance(project).findViewProvider(virtualFile)?.let {
                obtainLanguage(virtualFile)?.createFile(it)
            }
        }

    /**
     * Opens given file in editor.
     *
     * @param project current project
     * @param file    file to open
     */
    fun openFile(project: Project, file: PsiFile) {
        FileEditorManager.getInstance(project).openFile(file.virtualFile, true)
    }

    /**
     * Checks if given directory is VCS directory.
     *
     * @param directory to check
     * @return given file is VCS directory
     */
    fun isVcsDirectory(directory: VirtualFile) = when {
        !directory.isDirectory -> false
        else -> IgnoreBundle.VCS_LANGUAGES.find {
            directory.name == it.vcsDirectory && IgnoreBundle.ENABLED_LANGUAGES[it.fileType]!!
        } != null
    }

    /**
     * Gets list of words for given [String] excluding special characters.
     *
     * @param filter input string
     * @return list of words without special characters
     */
    fun getWords(filter: String) = filter.toLowerCase().split("\\W+").filter(String::isNotEmpty)

    /**
     * Checks if lists are equal.
     *
     * @param l1 first list
     * @param l2 second list
     * @return lists are equal
     */
    fun equalLists(l1: List<*>, l2: List<*>) = l1.size == l2.size && l1.containsAll(l2) && l2.containsAll(l1)

    /**
     * Searches for the module in the project that contains given file.
     *
     * @param file    file
     * @param project project
     * @return module containing passed file or null
     */
    fun getModuleForFile(file: VirtualFile, project: Project): Module? = ContainerUtil.find(
        ModuleManager.getInstance(project).modules
    ) { module: Module -> module.moduleContentScope.contains(file) }

    fun getModuleRootForFile(file: VirtualFile, project: Project) = getModuleForFile(file, project)?.let { module ->
        ModuleRootManager.getInstance(module).contentRoots.first()?.takeIf { it.isDirectory }
    }

    /**
     * Checks if file is in project directory.
     *
     * @param file    file
     * @param project project
     * @return file is under directory
     */
    fun isInProject(file: VirtualFile, project: Project) =
        getModuleForFile(file, project) != null || StringUtil.startsWith(file.url, "temp://")

    /**
     * Creates and configures template preview editor.
     *
     * @param document virtual editor document
     * @param project  current project
     * @return editor
     */
    fun createPreviewEditor(document: Document, project: Project?, isViewer: Boolean): Editor {
        val editor = EditorFactory.getInstance().createEditor(
            document,
            project,
            IgnoreFileType.INSTANCE,
            isViewer
        ) as EditorEx
        editor.setCaretEnabled(!isViewer)
        val settings = editor.settings
        settings.isLineNumbersShown = false
        settings.additionalColumnsCount = 1
        settings.additionalLinesCount = 0
        settings.isRightMarginShown = false
        settings.isFoldingOutlineShown = false
        settings.isLineMarkerAreaShown = false
        settings.isIndentGuidesShown = false
        settings.isVirtualSpace = false
        settings.isWheelFontChangeEnabled = false
        val colorsScheme = editor.colorsScheme
        colorsScheme.setColor(EditorColors.CARET_ROW_COLOR, null)
        return editor
    }
}
