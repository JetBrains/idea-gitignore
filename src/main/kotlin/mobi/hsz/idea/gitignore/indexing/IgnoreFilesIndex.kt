// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.indexing.IgnoreEntryOccurrence.Companion.deserialize
import mobi.hsz.idea.gitignore.indexing.IgnoreEntryOccurrence.Companion.serialize
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor
import mobi.hsz.idea.gitignore.util.Glob
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.util.Collections

/**
 * Implementation of [AbstractIgnoreFilesIndex] that allows to index all ignore files content using native
 * IDE mechanisms and increase indexing performance.
 */
class IgnoreFilesIndex : AbstractIgnoreFilesIndex<IgnoreFileTypeKey, IgnoreEntryOccurrence>() {

    companion object {
        val KEY = ID.create<IgnoreFileTypeKey, IgnoreEntryOccurrence>("IgnoreFilesIndex")
        private const val VERSION = 5
        private val DATA_EXTERNALIZER = object : DataExternalizer<IgnoreEntryOccurrence> {

            @Throws(IOException::class)
            override fun save(out: DataOutput, entry: IgnoreEntryOccurrence) {
                serialize(out, entry)
            }

            @Throws(IOException::class)
            override fun read(input: DataInput) = deserialize(input)
        }

        /**
         * Returns collection of indexed [IgnoreEntryOccurrence] for given [Project] and [IgnoreFileType].
         *
         * @param project  current project
         * @param fileType filetype
         * @return [IgnoreEntryOccurrence] collection
         */
        fun getEntries(project: Project, fileType: IgnoreFileType): List<IgnoreEntryOccurrence> {
            try {
                if (ApplicationManager.getApplication().isReadAccessAllowed) {
                    val scope = IgnoreSearchScope[project]
                    return FileBasedIndex.getInstance().getValues(KEY, IgnoreFileTypeKey(fileType), scope)
                }
            } catch (ignored: RuntimeException) {
            }
            return ContainerUtil.emptyList()
        }

        /**
         * Returns collection of indexed [VirtualFile] for given [Project] and [IgnoreFileTypeKey].
         *
         * @param project  current project
         * @param fileType filetype
         * @return [VirtualFile] collection
         */
        fun getFiles(project: Project, fileType: IgnoreFileType): List<VirtualFile> =
            getEntries(project, fileType).mapNotNull(IgnoreEntryOccurrence::file)
    }

    override fun getName(): ID<IgnoreFileTypeKey, IgnoreEntryOccurrence> = KEY

    override fun map(inputData: FileContent): Map<IgnoreFileTypeKey, IgnoreEntryOccurrence> {
        val inputDataPsi = try {
            inputData.psiFile
        } catch (e: Exception) {
            // if there is some stale indices (e.g. for mobi.hsz.idea.gitignore.lang.kind.GitLanguage)
            // inputData.getPsiFile() could throw exception that should be avoided
            return emptyMap()
        }
        if (inputDataPsi !is IgnoreFile) {
            return emptyMap()
        }

        val items = mutableListOf<Pair<String, Boolean>>()
        inputDataPsi.acceptChildren(
            object : IgnoreVisitor() {
                override fun visitEntry(entry: IgnoreEntry) {
                    val regex = Glob.getRegex(entry.value, entry.syntax, false)
                    items.add(Pair.create(regex, entry.isNegated))
                }
            }
        )

        return Collections.singletonMap(
            IgnoreFileTypeKey((inputData.fileType as IgnoreFileType)),
            IgnoreEntryOccurrence(inputData.file.url, items)
        )
    }

    @Synchronized
    @Throws(IOException::class)
    override fun save(out: DataOutput, value: IgnoreFileTypeKey) {
        out.writeUTF(value.type.languageName)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun read(input: DataInput) = IgnoreBundle.LANGUAGES
        .asSequence()
        .map { it.fileType }
        .firstOrNull { it.languageName == input.readUTF() }
        ?.let { IgnoreFileTypeKey(it) }

    override fun getValueExternalizer() = DATA_EXTERNALIZER

    override fun getVersion() = VERSION

    override fun acceptInput(file: VirtualFile) =
        file.fileType is IgnoreFileType || IgnoreManager.FILE_TYPES_ASSOCIATION_QUEUE.containsKey(file.name)
}
