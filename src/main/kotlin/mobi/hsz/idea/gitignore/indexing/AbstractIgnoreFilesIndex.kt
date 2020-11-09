// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.project.DumbAware
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndex.InputFilter
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.KeyDescriptor

/**
 * Abstract class of [FileBasedIndexExtension] that contains base configuration for [IgnoreFilesIndex].
 */
abstract class AbstractIgnoreFilesIndex<K, V> :
    FileBasedIndexExtension<K, V>(), KeyDescriptor<K>, DataIndexer<K, V, FileContent?>, InputFilter, DumbAware {

    /**
     * Returns [DataIndexer] implementation.
     *
     * @return [DataIndexer] instance.
     */
    override fun getIndexer() = this

    /**
     * Returns [KeyDescriptor] implementation.
     *
     * @return [KeyDescriptor] instance.
     */
    override fun getKeyDescriptor() = this

    /**
     * Checks if given types objects are equal.
     *
     * @param val1 object to compare
     * @param val2 object to compare
     * @return objects are equal
     */
    override fun isEqual(val1: K, val2: K) = val1 == val2

    /**
     * Returns hashCode for given type object.
     *
     * @param value type object
     * @return object's hashCode
     */
    override fun getHashCode(value: K): Int = value.hashCode()

    /**
     * Current [AbstractIgnoreFilesIndex] depends on the file content.
     *
     * @return depends on file content
     */
    override fun dependsOnFileContent() = true

    /**
     * Returns [FileBasedIndex.InputFilter] implementation.
     *
     * @return [FileBasedIndex.InputFilter] instance.
     */
    override fun getInputFilter() = this
}
