// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.util.Pair
import com.jetbrains.rd.util.concurrentMapOf

/**
 * Wrapper for ConcurrentHashMap that allows to expire values after given time.
 */
class ExpiringMap<K, V>(private val time: Int) {

    private val map = concurrentMapOf<K, Pair<V, Long>>()

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

    operator fun set(key: K, value: V): V {
        val current = System.currentTimeMillis()
        map[key] = Pair.create(value, current)
        return value
    }

    fun clear() {
        map.clear()
    }

    fun getOrElse(key: K, defaultValue: V) = get(key) ?: defaultValue
}
