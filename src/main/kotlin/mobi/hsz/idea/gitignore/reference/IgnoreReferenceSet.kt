// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.reference

import com.intellij.openapi.components.service
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
import com.jetbrains.rd.util.concurrentMapOf
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.MatcherUtil
import mobi.hsz.idea.gitignore.util.Utils
import java.util.ArrayList

/**
 * [FileReferenceSet] definition class.
 */
class IgnoreReferenceSet(element: IgnoreEntry) : FileReferenceSet(element) {

    private val matcher = element.project.service<IgnoreMatcher>()

    override fun createFileReference(range: TextRange, index: Int, text: String) = IgnoreReference(this, range, index, text)

    override fun isEndingSlashNotAllowed() = false

    override fun computeDefaultContexts() = element.containingFile.parent?.let(::listOf) ?: super.computeDefaultContexts()

    override fun getLastReference() = super.getLastReference()?.let {
        when {
            it.canonicalText.endsWith(separatorString) && myReferences != null && myReferences.size > 1 ->
                myReferences[myReferences.size - 2]
            else -> null
        }
    }

    override fun couldBeConvertedTo(relative: Boolean) = false

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
        if (currentSlash + sepLen + sepLen < str.length &&
            str.substring(currentSlash + sepLen, currentSlash + sepLen + sepLen) == separatorString
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
                startInElement + currentSlash + sepLen,
                startInElement +
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

    inner class IgnoreReference(fileReferenceSet: FileReferenceSet, range: TextRange?, index: Int, text: String?) :
        FileReference(fileReferenceSet, range, index, text) {
        private val cacheMap = concurrentMapOf<String, Collection<VirtualFile>>()

        override fun innerResolveInContext(
            text: String,
            context: PsiFileSystemItem,
            result: MutableCollection<ResolveResult>,
            caseSensitive: Boolean
        ) {
            ProgressManager.checkCanceled()
            super.innerResolveInContext(text, context, result, caseSensitive)
            val containingFile = containingFile as? IgnoreFile ?: return
            val contextVirtualFile: VirtualFile?

            when {
                Utils.isInProject(containingFile.virtualFile, element.project) -> {
                    contextVirtualFile = context.virtualFile
                }
                else -> return
            }
            if (contextVirtualFile != null) {
                val entry = fileReferenceSet.element as IgnoreEntry
                val current = canonicalText
                val pattern = Glob.createPattern(current, entry.syntax) ?: return
                val root = element.containingFile.parent?.virtualFile
                val psiManager = element.manager

                ContainerUtil.createLockFreeCopyOnWriteList<VirtualFile>().run {
                    addAll(MatcherUtil.getFilesForPattern(context.project, pattern))
                    if (isEmpty()) {
                        addAll(context.virtualFile.children)
                    } else if (current.endsWith(Constants.STAR) && current != entry.text) {
                        addAll(ContainerUtil.filter(context.virtualFile.children) { obj: VirtualFile -> obj.isDirectory })
                    } else if (current.endsWith(Constants.DOUBLESTAR)) {
                        val key = entry.text
                        if (!cacheMap.containsKey(key)) {
                            val children = mutableListOf<VirtualFile>()
                            val visitor: VirtualFileVisitor<*> = object : VirtualFileVisitor<Any?>() {
                                override fun visitFile(file: VirtualFile): Boolean {
                                    if (file.isDirectory) {
                                        children.add(file)
                                        return true
                                    }
                                    return false
                                }
                            }
                            forEach { file ->
                                ProgressManager.checkCanceled()
                                if (!file.isDirectory) {
                                    return@forEach
                                }
                                VfsUtil.visitChildrenRecursively(file, visitor)
                                children.remove(file)
                            }
                            cacheMap[key] = children
                        }
                        clear()
                        addAll(cacheMap[key]!!)
                    }

                    forEach { file ->
                        ProgressManager.checkCanceled()
                        if (Utils.isVcsDirectory(file)) {
                            return@forEach
                        }
                        val name = if (root != null) Utils.getRelativePath(root, file) else file.name
                        if (matcher.match(pattern, name)) {
                            val psiFileSystemItem = getPsiFileSystemItem(psiManager, file) ?: return@forEach
                            result.add(PsiElementResolveResult(psiFileSystemItem))
                        }
                    }
                }
            }
        }

        private fun getPsiFileSystemItem(manager: PsiManager, file: VirtualFile): PsiFileSystemItem? {
            if (!file.isValid) {
                return null
            }
            return manager.findDirectory(file).takeIf { file.isDirectory } ?: manager.findFile(file)
        }
    }
}
