// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.util.containers.ContainerUtil
import java.util.concurrent.ConcurrentMap

/**
 * [ConcurrentMap] wrapper with additional ability to cache values.
 */
class CachedConcurrentMap<K, V> private constructor(private val fetcher: DataFetcher<K, V>) {

    private val map: ConcurrentMap<K, V> = ContainerUtil.createConcurrentWeakMap()

    companion object {
        fun <K, V> create(fetcher: DataFetcher<K, V>) = CachedConcurrentMap(fetcher)
    }

    operator fun get(key: K): V? {
        if (!map.containsKey(key)) {
            map[key] = fetcher.fetch(key)
        }
        return map[key]
    }

    fun remove(key: K) {
        map.remove(key)
    }

    fun clear() {
        map.clear()
    }

    /** Fetcher interface. */
    fun interface DataFetcher<K, V> {
        /**
         * Fetches data for the given key.
         *
         * @param key key
         * @return value
         */
        fun fetch(key: K): V
    }
}
