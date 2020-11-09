// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.util.Pair
import java.util.concurrent.ConcurrentHashMap

/**
 * Wrapper for [ConcurrentHashMap] that allows to expire values after given time.
 *
 * @param <K> map key type
 * @param <V> map key value
</V></K> */
class ExpiringMap<K, V>(private val time: Int) {

    /** Cache map.  */
    private val map = ConcurrentHashMap<K, Pair<V, Long>>()

    /**
     * Gets value using passed key. Returns null if expired.
     *
     * @param key to check
     * @return value or `null` if expired
     */
    operator fun get(key: K): V? {
        val current = System.currentTimeMillis()
        map[key]?.let {
            if (it.getSecond() + time > current) {
                return it.getFirst()
            }
            map.remove(key)
        }
        return null
    }

    /**
     * Stores value under given key and resets expiration counter.
     *
     * @param key   to set
     * @param value to set
     * @return added value
     */
    operator fun set(key: K, value: V): V {
        val current = System.currentTimeMillis()
        map[key] = Pair.create(value, current)
        return value
    }

    /** Clears [.map].  */
    fun clear() {
        map.clear()
    }

    /**
     * Returns value under given key or default.
     *
     * @param key          to check
     * @param defaultValue value if null
     * @return value or default
     */
    fun getOrElse(key: K, defaultValue: V) = get(key) ?: defaultValue
}
