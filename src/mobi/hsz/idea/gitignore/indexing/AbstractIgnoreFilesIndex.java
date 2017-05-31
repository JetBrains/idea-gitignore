/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.indexing;

import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileBasedIndexExtension;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class of {@link FileBasedIndexExtension} that contains base configuration for {@link IgnoreFilesIndex}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public abstract class AbstractIgnoreFilesIndex<K, V> extends FileBasedIndexExtension<K, V>
        implements KeyDescriptor<K>, DataIndexer<K, V, FileContent>, FileBasedIndex.InputFilter {

    /**
     * Returns {@link DataIndexer} implementation.
     *
     * @return {@link DataIndexer} instance.
     */
    @NotNull
    @Override
    public DataIndexer<K, V, FileContent> getIndexer() {
        return this;
    }

    /**
     * Returns {@link KeyDescriptor} implementation.
     *
     * @return {@link KeyDescriptor} instance.
     */
    @NotNull
    @Override
    public KeyDescriptor<K> getKeyDescriptor() {
        return this;
    }

    /**
     * Checks if given types objects are equal.
     *
     * @param val1 object to compare
     * @param val2 object to compare
     * @return objects are equal
     */
    @Override
    public boolean isEqual(K val1, K val2) {
        return val1.equals(val2);
    }

    /**
     * Returns hashCode for given type object.
     *
     * @param value type object
     * @return object's hashCode
     */
    @Override
    public int getHashCode(K value) {
        return value.hashCode();
    }

    /**
     * Current {@link AbstractIgnoreFilesIndex} depends on the file content.
     *
     * @return depends on file content
     */
    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    /**
     * Returns {@link FileBasedIndex.InputFilter} implementation.
     *
     * @return {@link FileBasedIndex.InputFilter} instance.
     */
    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return this;
    }
}
