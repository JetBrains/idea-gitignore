// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.io.IOUtil
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
                IOUtil.writeUTF(this, entry.url)
                writeInt(entry.items.size)
                entry.items.forEach {
                    IOUtil.writeUTF(this, it.first)
                    writeBoolean(it.second)
                }
            }
        }

        @Synchronized
        @Throws(IOException::class)
        fun deserialize(input: DataInput): IgnoreEntryOccurrence {
            val url = IOUtil.readUTF(input)
            val items = mutableListOf<Pair<String, Boolean>>()

            if (url.isNotEmpty()) {
                val size = input.readInt()
                repeat((0 until size).count()) {
                    items.add(Pair.create(IOUtil.readUTF(input), input.readBoolean()))
                }
            }
            return IgnoreEntryOccurrence(url, items)
        }
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        for (item in items) {
            result = 31 * result + (item.first?.hashCode() ?: 0)
            result = 31 * result + (item.second?.hashCode() ?: 0)
        }
        return result
    }

    override fun equals(other: Any?) = when {
        other !is IgnoreEntryOccurrence -> false
        url != other.url || items.size != other.items.size -> false
        else -> items.indices.find { items[it].toString() != other.items[it].toString() } == null
    }
}
