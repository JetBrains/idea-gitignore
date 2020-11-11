// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.util.containers.IntObjectCache
import org.apache.commons.lang.builder.HashCodeBuilder
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Util class to speed up and limit regex operation on the files paths.
 */
class MatcherUtil {

    private val cache = IntObjectCache<Boolean>()

    /**
     * Extracts alphanumeric parts from the regex pattern and checks if any of them is contained in the tested path.
     * Looking for the parts speed ups the matching and prevents from running whole regex on the string.
     *
     * @param pattern to explode
     * @param path    to check
     * @return path matches the pattern
     */
    fun match(pattern: Pattern?, path: String?): Boolean {
        if (pattern == null || path == null) {
            return false
        }
        synchronized(cache) {
            val hashCode = HashCodeBuilder().append(pattern).append(path).toHashCode()
            if (!cache.containsKey(hashCode)) {
                val parts = getParts(pattern)
                var result = false
                if (parts.isEmpty() || matchAllParts(parts, path)) {
                    try {
                        result = pattern.matcher(path).find()
                    } catch (ignored: StringIndexOutOfBoundsException) {
                    }
                }
                cache.put(hashCode, result)
                return result
            }
            return cache[hashCode]
        }
    }

    companion object {
        /**
         * Checks if given path contains all of the path parts.
         *
         * @param parts that should be contained in path
         * @param path  to check
         * @return path contains all parts
         */
        fun matchAllParts(parts: Array<String?>?, path: String?): Boolean {
            var index = -1
            parts?.forEach {
                index = path?.indexOf(it!!, index) ?: -1
                if (index == -1) {
                    return false
                }
            } ?: return false
            return true
        }

        /**
         * Checks if given path contains any of the path parts.
         *
         * @param parts that should be contained in path
         * @param path  to check
         * @return path contains any of the parts
         */
        fun matchAnyPart(parts: Array<String?>?, path: String?) = parts?.any { path?.contains(it!!) ?: false } ?: false

        /**
         * Extracts alphanumeric parts from  [Pattern].
         *
         * @param pattern to handle
         * @return extracted parts
         */
        fun getParts(pattern: Pattern?): Array<String?> {
            if (pattern == null) {
                return arrayOfNulls(0)
            }
            val parts: MutableList<String?> = ArrayList()
            val sPattern = pattern.toString()
            var part = StringBuilder()
            var inSquare = false
            for (i in sPattern.indices) {
                val ch = sPattern[i]
                if (!inSquare && Character.isLetterOrDigit(ch)) {
                    part.append(sPattern[i])
                } else if (part.isNotEmpty()) {
                    parts.add(part.toString())
                    part = StringBuilder()
                }
                inSquare = ch != ']' && (ch == '[' || inSquare)
            }
            return parts.toTypedArray()
        }
    }
}
