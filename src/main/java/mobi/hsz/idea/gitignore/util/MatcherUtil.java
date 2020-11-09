// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util;

import com.intellij.util.containers.IntObjectCache;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Util class to speed up and limit regex operation on the files paths.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.3.1
 */
public class MatcherUtil {
    /** Stores calculated matching results. */
    private final IntObjectCache<Boolean> cache = new IntObjectCache<>();

    /**
     * Extracts alphanumeric parts from the regex pattern and checks if any of them is contained in the tested path.
     * Looking for the parts speed ups the matching and prevents from running whole regex on the string.
     *
     * @param pattern to explode
     * @param path    to check
     * @return path matches the pattern
     */
    public boolean match(@Nullable Pattern pattern, @Nullable String path) {
        if (pattern == null || path == null) {
            return false;
        }

        synchronized (cache) {
            int hashCode = new HashCodeBuilder().append(pattern).append(path).toHashCode();

            if (!cache.containsKey(hashCode)) {
                final String[] parts = getParts(pattern);
                boolean result = false;

                if (parts.length == 0 || matchAllParts(parts, path)) {
                    try {
                        result = pattern.matcher(path).find();
                    } catch (StringIndexOutOfBoundsException ignored) {
                    }
                }

                cache.put(hashCode, result);
                return result;
            }

            return cache.get(hashCode);
        }
    }

    /**
     * Checks if given path contains all of the path parts.
     *
     * @param parts that should be contained in path
     * @param path  to check
     * @return path contains all parts
     */
    public static boolean matchAllParts(@Nullable String[] parts, @Nullable String path) {
        if (parts == null || path == null) {
            return false;
        }

        int index = -1;
        for (String part : parts) {
            index = path.indexOf(part, index);
            if (index == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if given path contains any of the path parts.
     *
     * @param parts that should be contained in path
     * @param path  to check
     * @return path contains any of the parts
     */
    public static boolean matchAnyPart(@Nullable String[] parts, @Nullable String path) {
        if (parts == null || path == null) {
            return false;
        }

        for (String part : parts) {
            if (path.contains(part)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Extracts alphanumeric parts from  {@link Pattern}.
     *
     * @param pattern to handle
     * @return extracted parts
     */
    @NotNull
    public static String[] getParts(@Nullable Pattern pattern) {
        if (pattern == null) {
            return new String[0];
        }

        final List<String> parts = new ArrayList<>();
        final String sPattern = pattern.toString();

        StringBuilder part = new StringBuilder();
        boolean inSquare = false;
        for (int i = 0; i < sPattern.length(); i++) {
            char ch = sPattern.charAt(i);
            if (!inSquare && Character.isLetterOrDigit(ch)) {
                part.append(sPattern.charAt(i));
            } else if (part.length() > 0) {
                parts.add(part.toString());
                part = new StringBuilder();
            }

            inSquare = ch != ']' && ((ch == '[') || inSquare);
        }

        return parts.toArray(new String[0]);
    }
}
