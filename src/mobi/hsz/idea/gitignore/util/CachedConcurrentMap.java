/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.util;

import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentMap;

/**
 * {@link ConcurrentMap} wrapper with additional ability to cache values.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.1.0
 */
public class CachedConcurrentMap<K, V> {
    /** Cache map. */
    @NotNull
    private final ConcurrentMap<K, V> map;

    /** Data fetcher instance invoked if key is not filled with value. */
    @NotNull
    private final DataFetcher<K, V> fetcher;

    /**
     * Default constructor.
     *
     * @param fetcher data fetcher
     */
    private CachedConcurrentMap(@NotNull DataFetcher<K, V> fetcher) {
        this.map = ContainerUtil.createConcurrentWeakMap();
        this.fetcher = fetcher;
    }

    /**
     * Instance creator.
     *
     * @param fetcher fetcher
     * @return instance of {@link CachedConcurrentMap}
     */
    @NotNull
    public static <K, V> CachedConcurrentMap<K, V> create(@NotNull DataFetcher<K, V> fetcher) {
        return new CachedConcurrentMap<>(fetcher);
    }

    /**
     * Returns value set under the given key or invokes {@link DataFetcher#fetch(Object)} if not.
     *
     * @param key data key
     * @return value
     */
    public V get(@NotNull K key) {
        if (!map.containsKey(key)) {
            map.put(key, fetcher.fetch(key));
        }
        return map.get(key);
    }

    /**
     * Removes value using given key.
     *
     * @param key current key
     */
    public void remove(@NotNull K key) {
        this.map.remove(key);
    }

    /** Clears cache map. */
    public void clear() {
        this.map.clear();
    }

    /** Fetcher interface. */
    public interface DataFetcher<K, V> {
        /**
         * Fetches data for the given key.
         *
         * @param key key
         * @return value
         */
        V fetch(@NotNull K key);
    }
}
