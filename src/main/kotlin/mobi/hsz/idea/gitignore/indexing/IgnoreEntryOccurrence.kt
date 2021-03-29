// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.HashCodeBuilder
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.io.Serializable

/**
 * Entry containing information about the [VirtualFile] instance of the ignore file mapped with the collection
 * of ignore entries for better performance. Class is used for indexing.
 */
@Suppress("SerialVersionUIDInSerializableClass")
class IgnoreEntryOccurrence(private val url: String, val items: List<Pair<String, Boolean>>) : Serializable {

    /**
     * Returns current [VirtualFile].
     *
     * @return current file
     */
    var file: VirtualFile? = null
        get() {
            if (field == null && url.isNotEmpty()) {
                field = VirtualFileManager.getInstance().findFileByUrl(url)
            }
            return field
        }
        private set

    companion object {
        @Synchronized
        @Throws(IOException::class)
        fun serialize(out: DataOutput, entry: IgnoreEntryOccurrence) {
            out.run {
                writeUTF(entry.url)
                writeInt(entry.items.size)
                entry.items.forEach {
                    writeUTF(it.first)
                    writeBoolean(it.second)
                }
            }
        }

        @Synchronized
        @Throws(IOException::class)
        fun deserialize(input: DataInput): IgnoreEntryOccurrence {
            val url = input.readUTF()
            val items = mutableListOf<Pair<String, Boolean>>()

            if (!StringUtils.isEmpty(url)) {
                val size = input.readInt()
                repeat((0 until size).count()) {
                    items.add(Pair.create(input.readUTF(), input.readBoolean()))
                }
            }
            return IgnoreEntryOccurrence(url, items)
        }
    }

    override fun hashCode() = HashCodeBuilder().append(url).apply {
        items.forEach { append(it.first).append(it.second) }
    }.toHashCode()

    override fun equals(other: Any?) = when {
        other !is IgnoreEntryOccurrence -> false
        url != other.url || items.size != other.items.size -> false
        else -> items.indices.find { items[it].toString() != other.items[it].toString() } == null
    }
}
