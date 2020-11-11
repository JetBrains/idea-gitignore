// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.ide.plugins.IdeaPluginDescriptorImpl
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.addIfNotNull
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreBundle.obtainLanguage
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
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
     * Gets Ignore file for given [Project] and root [PsiDirectory].
     * If file is missing - creates new one.
     *
     * @param project         current project
     * @param fileType        current ignore file type
     * @param createIfMissing create new file if missing
     * @return Ignore file
     */
    fun getIgnoreFile(project: Project, fileType: IgnoreFileType, psiDirectory: PsiDirectory?, createIfMissing: Boolean): PsiFile? {
        val projectDir = guessProjectDir(project) ?: return null
        val directory = psiDirectory ?: PsiManager.getInstance(project).findDirectory(projectDir)
        assert(directory != null)

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
        openFile(project, file.virtualFile)
    }

    /**
     * Opens given file in editor.
     *
     * @param project current project
     * @param file    file to open
     */
    fun openFile(project: Project, file: VirtualFile) {
        FileEditorManager.getInstance(project).openFile(file, true)
    }

    /**
     * Returns all Ignore files in given [Project] that can match current passed file.
     *
     * @param project current project
     * @param file    current file
     * @return collection of suitable Ignore files
     */
    @Throws(ExternalFileException::class)
    fun getSuitableIgnoreFiles(project: Project, fileType: IgnoreFileType, virtualFile: VirtualFile): List<VirtualFile> {
        var file = virtualFile
        val baseDir = getModuleRootForFile(file, project)
        val files = mutableListOf<VirtualFile>()

        if (file.canonicalPath == null || baseDir == null || !VfsUtilCore.isAncestor(baseDir, file, true)) {
            throw ExternalFileException()
        }

        if (baseDir != file) {
            do {
                file = file.parent
                files.addIfNotNull(file.findChild(fileType.ignoreLanguage.filename))
            } while (file != baseDir)
        }

        return files
    }

    /**
     * Checks if given directory is a [IgnoreLanguage.getVcsDirectory].
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
     * Searches for excluded roots in given [Project].
     *
     * @param project current project
     * @return list of excluded roots
     */
    fun getExcludedRoots(project: Project) = mutableListOf<VirtualFile>().apply {
        ModuleManager.getInstance(project).modules.forEach {
            ModuleRootManager.getInstance(it!!).modifiableModel.run {
                if (isDisposed) {
                    return@forEach
                }
                addAll(excludeRoots)
                dispose()
            }
        }
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
     * Returns [IgnoreFileType] basing on the [VirtualFile] file.
     *
     * @param virtualFile current file
     * @return file type
     */
    fun getFileType(virtualFile: VirtualFile?) = virtualFile?.run {
        fileType.takeIf { it is IgnoreFileType } as IgnoreFileType
    }

    /**
     * Checks if file is under given directory.
     *
     * @param file      file
     * @param directory directory
     * @return file is under directory
     */
    fun isUnder(file: VirtualFile, directory: VirtualFile): Boolean {
        if (directory == file) {
            return true
        }
        var parent = file.parent
        while (parent != null) {
            if (directory == parent) {
                return true
            }
            parent = parent.parent
        }
        return false
    }

    /**
     * Searches for the module in the project that contains given file.
     *
     * @param file    file
     * @param project project
     * @return module containing passed file or null
     */
    fun getModuleForFile(file: VirtualFile, project: Project): Module? {
        return ContainerUtil.find(
            ModuleManager.getInstance(project).modules
        ) { module: Module -> module.moduleContentScope.contains(file) }
    }

    private fun getModuleRoot(module: Module) = ModuleRootManager.getInstance(module).contentRoots.first()?.takeIf { it.isDirectory }

    fun getModuleRootForFile(file: VirtualFile, project: Project): VirtualFile? {
        val module = getModuleForFile(file, project)
        return if (module == null) null else getModuleRoot(module)
    }

    /**
     * Checks if file is in project directory.
     *
     * @param file    file
     * @param project project
     * @return file is under directory
     */
    fun isInProject(file: VirtualFile, project: Project): Boolean {
        return getModuleForFile(file, project) != null || StringUtil.startsWith(file.url, "temp://")
    }

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

    /**
     * Checks if specified plugin is enabled.
     *
     * @param id plugin id
     * @return plugin is enabled
     */
    private fun isPluginEnabled(id: String): Boolean {
        val p = PluginManager.getPlugin(PluginId.getId(id))
        return p is IdeaPluginDescriptorImpl && p.isEnabled()
    }

    /**
     * Checks if Git plugin is enabled.
     *
     * @return Git plugin is enabled
     */
    val isGitPluginEnabled
        get() = isPluginEnabled("Git4Idea")

    /**
     * Checks if Mercurial plugin is enabled.
     *
     * @return Mercurial plugin is enabled
     */
    val isMercurialPluginEnabled
        get() = isPluginEnabled("hg4idea")

    /**
     * Resolves user directory with the `user.home` property.
     *
     * @param path path with leading ~
     * @return resolved path
     */
    fun resolveUserDir(path: String?): String? {
        if (StringUtil.startsWithChar(path, '~')) {
            assert(path != null)
            return System.getProperty("user.home") + path!!.substring(1)
        }
        return path
    }

    /**
     * Adds ColoredFragment to the node's presentation.
     *
     * @param data       node's presentation data
     * @param text       text to add
     * @param attributes custom [SimpleTextAttributes]
     */
    fun addColoredText(data: PresentationData, text: String, attributes: SimpleTextAttributes) {
        if (data.coloredText.isEmpty()) {
            data.addText(data.presentableText, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        data.addText(" $text", attributes)
    }

    /**
     * Wraps [ProjectUtil.guessProjectDir] and returns null for the default project.
     *
     * @param project to check
     * @return project's dir or null if project is default
     */
    fun guessProjectDir(project: Project?): VirtualFile? {
        return if (project == null) {
            null
        } else try {
            project.guessProjectDir()
        } catch (e: IllegalStateException) {
            null
        }
    }
}
