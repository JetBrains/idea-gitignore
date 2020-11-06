// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.reference

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.FilesIndexCacheProjectComponent
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.Utils
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

/**
 * [FileReferenceSet] definition class.
 */
class IgnoreReferenceSet(element: IgnoreEntry) : FileReferenceSet(element) {

    private val filesIndexCache = FilesIndexCacheProjectComponent.getInstance(element.project)
    private val manager = IgnoreManager.getInstance(element.project)

    /**
     * Creates [IgnoreReference] instance basing on passed text value.
     *
     * @param range text range
     * @param index start index
     * @param text  string text
     * @return file reference
     */
    override fun createFileReference(range: TextRange, index: Int, text: String) =
        IgnoreReference(this, range, index, text)

    /**
     * Sets ending slash as allowed.
     *
     * @return `false`
     */
    override fun isEndingSlashNotAllowed() = false

    /**
     * Computes current element's parent context.
     *
     * @return contexts collection
     */
    override fun computeDefaultContexts() =
        element.containingFile.parent?.let(::listOf) ?: super.computeDefaultContexts()

    /**
     * Returns last reference of the current element's references.
     *
     * @return last [FileReference]
     */
    override fun getLastReference(): FileReference? {
        val lastReference = super.getLastReference()
        return if (lastReference != null && lastReference.canonicalText.endsWith(separatorString)) {
            if (myReferences != null && myReferences.size > 1) myReferences[myReferences.size - 2] else null
        } else lastReference
    }

    /**
     * Disallows conversion to relative reference.
     *
     * @param relative is ignored
     * @return `false`
     */
    override fun couldBeConvertedTo(relative: Boolean) = false

    /**
     * Parses entry, searches for file references and stores them in [.myReferences].
     */
    override fun reparse() {
        ProgressManager.checkCanceled()
        val str = StringUtil.trimEnd(pathString, separatorString)
        val referencesList: MutableList<FileReference?> = ArrayList()
        val separatorString = separatorString // separator's length can be more then 1 char
        val sepLen = separatorString.length
        var currentSlash = -sepLen
        val startInElement = startInElement

        // skip white space
        while (currentSlash + sepLen < str.length && Character.isWhitespace(str[currentSlash + sepLen])) {
            currentSlash++
        }
        if (currentSlash + sepLen + sepLen < str.length && str.substring(
                currentSlash + sepLen,
                currentSlash + sepLen + sepLen
            ) == separatorString
        ) {
            currentSlash += sepLen
        }
        var index = 0
        if (str == separatorString) {
            val fileReference = createFileReference(TextRange(startInElement, startInElement + sepLen), index++, separatorString)
            referencesList.add(fileReference)
        }
        while (true) {
            ProgressManager.checkCanceled()
            val nextSlash = str.indexOf(separatorString, currentSlash + sepLen)
            val subReferenceText = if (nextSlash > 0) str.substring(0, nextSlash) else str
            val range = TextRange(
                startInElement + currentSlash + sepLen, startInElement +
                    if (nextSlash > 0) nextSlash else str.length
            )
            val ref = createFileReference(range, index++, subReferenceText)
            referencesList.add(ref)
            if (nextSlash.also { currentSlash = it } < 0) {
                break
            }
        }
        myReferences = referencesList.toTypedArray()
    }

    /**
     * Custom definition of [FileReference].
     */
    inner class IgnoreReference(fileReferenceSet: FileReferenceSet, range: TextRange?, index: Int, text: String?) :
        FileReference(fileReferenceSet, range, index, text) {
        /**
         * Concurrent cache map.
         */
        private val cacheMap = ConcurrentHashMap<String, Collection<VirtualFile>>()

        /**
         * Resolves reference to the filesystem.
         *
         * @param text          entry
         * @param context       filesystem context
         * @param result        result references collection
         * @param caseSensitive is ignored
         */
        override fun innerResolveInContext(
            text: String, context: PsiFileSystemItem,
            result: MutableCollection<ResolveResult>, caseSensitive: Boolean
        ) {
            ProgressManager.checkCanceled()
            super.innerResolveInContext(text, context, result, caseSensitive)
            val containingFile = containingFile as? IgnoreFile ?: return
            val contextVirtualFile: VirtualFile?
            val isOuterFile = isOuterFile(containingFile)

            when {
                isOuterFile -> {
                    contextVirtualFile = Utils.getModuleRootForFile(
                        containingFile.virtualFile,
                        containingFile.project
                    )
                    result.clear()
                }
                Utils.isInProject(containingFile.virtualFile, element.project) -> {
                    contextVirtualFile = context.virtualFile
                }
                else -> {
                    return
                }
            }
            if (contextVirtualFile != null) {
                val entry = fileReferenceSet.element as IgnoreEntry
                val current = canonicalText
                val pattern = Glob.createPattern(current, entry.syntax)
                if (pattern != null) {
                    val parent = element.containingFile.parent
                    val root = if (isOuterFile) contextVirtualFile else parent?.virtualFile
                    val psiManager = element.manager
                    val files = ContainerUtil.createLockFreeCopyOnWriteList<VirtualFile>()
                    files.addAll(filesIndexCache.getFilesForPattern(context.project, pattern))
                    if (files.isEmpty()) {
                        files.addAll(ContainerUtil.newArrayList(*context.virtualFile.children))
                    } else if (current.endsWith(Constants.STAR) && current != entry.text) {
                        files.addAll(ContainerUtil.filter(
                            context.virtualFile.children
                        ) { obj: VirtualFile -> obj.isDirectory })
                    } else if (current.endsWith(Constants.DOUBLESTAR)) {
                        val key = entry.text
                        if (!cacheMap.containsKey(key)) {
                            val children: MutableCollection<VirtualFile> = ArrayList()
                            val visitor: VirtualFileVisitor<*> = object : VirtualFileVisitor<Any?>() {
                                override fun visitFile(file: VirtualFile): Boolean {
                                    if (file.isDirectory) {
                                        children.add(file)
                                        return true
                                    }
                                    return false
                                }
                            }
                            for (file in files) {
                                ProgressManager.checkCanceled()
                                if (!file.isDirectory) {
                                    continue
                                }
                                VfsUtil.visitChildrenRecursively(file, visitor)
                                children.remove(file)
                            }
                            cacheMap[key] = children
                        }
                        files.clear()
                        files.addAll(cacheMap[key]!!)
                    }
                    for (file in files) {
                        ProgressManager.checkCanceled()
                        if (Utils.isVcsDirectory(file)) {
                            continue
                        }
                        val name = if (root != null) Utils.getRelativePath(root, file) else file.name
                        if (manager.matcher.match(pattern, name)) {
                            val psiFileSystemItem = getPsiFileSystemItem(psiManager, file) ?: continue
                            result.add(PsiElementResolveResult(psiFileSystemItem))
                        }
                    }
                }
            }
        }

        /**
         * Checks if [IgnoreFile] is defined as an outer rules file.
         *
         * @param file current file
         * @return is outer file
         */
        private fun isOuterFile(file: IgnoreFile?) = file != null && file.isOuter

        /**
         * Searches for directory or file using [PsiManager].
         *
         * @param manager [PsiManager] instance
         * @param file    working file
         * @return Psi item
         */
        private fun getPsiFileSystemItem(manager: PsiManager, file: VirtualFile): PsiFileSystemItem? {
            if (!file.isValid) {
                return null
            }
            return if (file.isDirectory) manager.findDirectory(file) else manager.findFile(file)
        }
    }
}
