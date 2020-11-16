// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.project.DumbAware
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex.InputFilter
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.KeyDescriptor

/**
 * Abstract class of [FileBasedIndexExtension] that contains base configuration for [IgnoreFilesIndex].
 */
abstract class AbstractIgnoreFilesIndex<K, V> :
    FileBasedIndexExtension<K, V>(),
    KeyDescriptor<K>,
    DataIndexer<K, V, FileContent?>,
    InputFilter,
    DumbAware {

    override fun getIndexer() = this

    override fun getKeyDescriptor() = this

    override fun isEqual(val1: K, val2: K) = val1 == val2

    override fun getHashCode(value: K): Int = value.hashCode()

    override fun dependsOnFileContent() = true

    override fun getInputFilter() = this
}
