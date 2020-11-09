/// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util;

import org.jetbrains.annotations.NotNull;

/**
 * Abstracts a listenable object.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0.3
 */
public interface Listenable<T> {
    /**
     * Add the given listener. The listener will be executed in the containing instance's thread.
     *
     * @param listener listener to add
     */
    void addListener(@NotNull T listener);

    /**
     * Remove the given listener.
     *
     * @param listener listener to remove
     */
    void removeListener(@NotNull T listener);
}
