// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util;

import org.jetbrains.annotations.NonNls;

/**
 * Class containing common constant variables.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public class Constants {
    /** New line character. */
    @NonNls
    public static final String NEWLINE = "\n";

    /** Hash sign. */
    @NonNls
    public static final String HASH = "#";

    /** Dollar sign separator. */
    @NonNls
    public static final String DOLLAR = "$";

    /** Star sign. */
    @NonNls
    public static final String STAR = "*";

    /** Star sign. */
    @NonNls
    public static final String DOUBLESTAR = "**";

    /** Private constructor to prevent creating {@link Utils} instance. */
    private Constants() {
    }
}
