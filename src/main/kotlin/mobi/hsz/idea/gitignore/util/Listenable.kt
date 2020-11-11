/// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

/**
 * Abstracts a listenable object.
 */
interface Listenable<T> {

    /**
     * Add the given listener. The listener will be executed in the containing instance's thread.
     *
     * @param listener listener to add
     */
    fun addListener(listener: T)

    /**
     * Remove the given listener.
     *
     * @param listener listener to remove
     */
    fun removeListener(listener: T)
}
