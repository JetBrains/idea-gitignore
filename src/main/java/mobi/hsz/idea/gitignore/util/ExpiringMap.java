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

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Wrapper for {@link ConcurrentHashMap} that allows to expire values after given time.
 *
 * @param <K> map key type
 * @param <V> map key value
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0.5
 */
public class ExpiringMap<K, V> {

    /** Time to expire. */
    private final int time;

    /** Cache map. */
    private final ConcurrentHashMap<K, Pair<V, Long>> map = new ConcurrentHashMap<>();

    /**
     * Constructor.
     *
     * @param time to expire
     */
    public ExpiringMap(int time) {
        this.time = time;
    }

    /**
     * Gets value using passed key. Returns null if expired.
     *
     * @param key to check
     * @return value or <code>null</code> if expired
     */
    @Nullable
    public V get(@NotNull K key) {
        long current = System.currentTimeMillis();
        final Pair<V, Long> data = map.get(key);
        if (data != null) {
            if ((data.getSecond() + time) > current) {
                return data.getFirst();
            }
            map.remove(key);
        }
        return null;
    }

    /**
     * Stores value under given key and resets expiration counter.
     *
     * @param key   to set
     * @param value to set
     * @return added value
     */
    @NotNull
    public V set(@NotNull K key, @NotNull V value) {
        long current = System.currentTimeMillis();
        map.put(key, Pair.create(value, current));
        return value;
    }

    /** Clears {@link #map}. */
    public void clear() {
        map.clear();
    }

    /**
     * Returns value under given key or default.
     *
     * @param key          to check
     * @param defaultValue value if null
     * @return value or default
     */
    @NotNull
    public V getOrElse(@NotNull K key, @NotNull V defaultValue) {
        final V value = get(key);
        return value != null ? value : defaultValue;
    }
}
