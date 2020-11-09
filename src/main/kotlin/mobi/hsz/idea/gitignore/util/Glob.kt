// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.rd.util.concurrentMapOf
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.util.Utils.getRelativePath
import mobi.hsz.idea.gitignore.util.Utils.isVcsDirectory
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Glob util class that prepares glob statements or searches for content using glob rules.
 */
object Glob {

    /** Cache map that holds processed regex statements to the glob rules.  */
    private val GLOBS_CACHE = ContainerUtil.newConcurrentMap<String, String>()

    /** Cache map that holds compiled regex.  */
    private val PATTERNS_CACHE = ContainerUtil.newConcurrentMap<String, Pattern>()

    /**
     * Finds for [VirtualFile] list using glob rule in given root directory.
     *
     * @param root  root directory
     * @param entry ignore entry
     * @return search result
     */
    fun findOne(root: VirtualFile, entry: IgnoreEntry, matcher: MatcherUtil) =
        find(root, ContainerUtil.newArrayList(entry), matcher, false)[entry]?.first()

    /**
     * Finds for [VirtualFile] list using glob rule in given root directory.
     *
     * @param root          root directory
     * @param entries       ignore entries
     * @param includeNested attach children to the search result
     * @return search result
     */
    fun find(
        root: VirtualFile,
        entries: List<IgnoreEntry>,
        matcher: MatcherUtil,
        includeNested: Boolean
    ): Map<IgnoreEntry, MutableList<VirtualFile>> {
        val result = concurrentMapOf<IgnoreEntry, MutableList<VirtualFile>>()
        val map = concurrentMapOf<IgnoreEntry, Pattern>()

        entries.forEach {
            result[it] = mutableListOf()
            createPattern(it)?.let { pattern ->
                map[it] = pattern
            }
        }

        val visitor = object : VirtualFileVisitor<Map<IgnoreEntry, Pattern?>>(NO_FOLLOW_SYMLINKS) {
            override fun visitFile(file: VirtualFile): Boolean {
                if (root == file) {
                    return true
                }
                val current = mutableMapOf<IgnoreEntry, Pattern?>()
                if (currentValue.isEmpty()) {
                    return false
                }
                val path = getRelativePath(root, file)
                if (path == null || isVcsDirectory(file)) {
                    return false
                }

                currentValue.forEach { (key, value) ->
                    var matches = false
                    if (value == null || matcher.match(value, path)) {
                        matches = true
                        result[key]!!.add(file)
                    }
                    current[key] = value.takeIf { !includeNested || !matches }
                }

                setValueForChildren(current)
                return true
            }
        }

        visitor.setValueForChildren(map)
        VfsUtil.visitChildrenRecursively(root, visitor)

        return result
    }

    /**
     * Finds for [VirtualFile] paths list using glob rule in given root directory.
     *
     * @param root          root directory
     * @param entries       ignore entry
     * @param includeNested attach children to the search result
     * @return search result
     */
    fun findAsPaths(
        root: VirtualFile,
        entries: List<IgnoreEntry>,
        matcher: MatcherUtil,
        includeNested: Boolean
    ) = find(root, entries, matcher, includeNested).mapValues { (_, value) ->
        value
            .asSequence()
            .map { getRelativePath(root, it) }
            .filterNotNull()
            .toSet()
    }

    /**
     * Creates regex [Pattern] using [IgnoreEntry].
     *
     * @param entry          [IgnoreEntry]
     * @param acceptChildren Matches directory children
     * @return regex [Pattern]
     */
    fun createPattern(entry: IgnoreEntry, acceptChildren: Boolean = false) = createPattern(entry.value, entry.syntax, acceptChildren)

    /**
     * Creates regex [Pattern] using glob rule.
     *
     * @param rule   rule value
     * @param syntax rule syntax
     * @return regex [Pattern]
     */
    fun createPattern(rule: String, syntax: IgnoreBundle.Syntax, acceptChildren: Boolean = false) =
        getPattern(getRegex(rule, syntax, acceptChildren))

    /**
     * Returns regex string basing on the rule and provided syntax.
     *
     * @param rule           rule value
     * @param syntax         rule syntax
     * @param acceptChildren Matches directory children
     * @return regex string
     */
    fun getRegex(rule: String, syntax: IgnoreBundle.Syntax, acceptChildren: Boolean) = when (syntax) {
        IgnoreBundle.Syntax.GLOB -> createRegex(rule, acceptChildren)
        else -> rule
    }

    /**
     * Converts regex string to [Pattern] with caching.
     *
     * @param regex regex to convert
     * @return [Pattern] instance or null if invalid
     */
    fun getPattern(regex: String) = try {
        if (!PATTERNS_CACHE.containsKey(regex)) {
            PATTERNS_CACHE[regex] = Pattern.compile(regex)
        }
        PATTERNS_CACHE[regex]
    } catch (e: PatternSyntaxException) {
        null
    }

    /**
     * Creates regex [String] using glob rule.
     *
     * @param glob           rule
     * @param acceptChildren Matches directory children
     * @return regex [String]
     */
    fun createRegex(glob: String, acceptChildren: Boolean): String = glob.trim { it <= ' ' }.let {
        val cached = GLOBS_CACHE[it]
        if (cached != null) {
            return cached
        }

        val sb = StringBuilder("^")
        var escape = false
        var star = false
        var doubleStar = false
        var bracket = false
        var beginIndex = 0

        if (StringUtil.startsWith(it, Constants.DOUBLESTAR)) {
            sb.append("(?:[^/]*?/)*")
            beginIndex = 2
            doubleStar = true
        } else if (StringUtil.startsWith(it, "*/")) {
            sb.append("[^/]*")
            beginIndex = 1
            star = true
        } else if (StringUtil.equals(Constants.STAR, it)) {
            sb.append(".*")
        } else if (StringUtil.startsWithChar(it, '*')) {
            sb.append(".*?")
        } else if (StringUtil.startsWithChar(it, '/')) {
            beginIndex = 1
        } else {
            val slashes = StringUtil.countChars(it, '/')
            if (slashes == 0 || slashes == 1 && StringUtil.endsWithChar(it, '/')) {
                sb.append("(?:[^/]*?/)*")
            }
        }

        val chars = it.substring(beginIndex).toCharArray()
        for (ch in chars) {
            if (bracket && ch != ']') {
                sb.append(ch)
                continue
            } else if (doubleStar) {
                doubleStar = false
                if (ch == '/') {
                    sb.append("(?:[^/]*/)*?")
                    continue
                } else {
                    sb.append("[^/]*?")
                }
            }
            if (ch == '*') {
                if (escape) {
                    sb.append("\\*")
                    star = false
                    escape = star
                } else if (star) {
                    val prev = if (sb.isNotEmpty()) sb[sb.length - 1] else '\u0000'
                    if (prev == '\u0000' || prev == '^' || prev == '/') {
                        doubleStar = true
                    } else {
                        sb.append("[^/]*?")
                    }
                    star = false
                } else {
                    star = true
                }
                continue
            } else if (star) {
                sb.append("[^/]*?")
                star = false
            }
            when (ch) {
                '\\' -> {
                    if (escape) {
                        sb.append("\\\\")
                    }
                    escape = !escape
                }
                '?' ->
                    if (escape) {
                        sb.append("\\?")
                        escape = false
                    } else {
                        sb.append('.')
                    }
                '[' -> {
                    if (escape) {
                        sb.append('\\')
                        escape = false
                    } else {
                        bracket = true
                    }
                    sb.append(ch)
                }
                ']' -> {
                    if (!bracket) {
                        sb.append('\\')
                    }
                    sb.append(ch)
                    bracket = false
                    escape = false
                }
                '.', '(', ')', '{', '}', '+', '|', '^', '$', '@', '%' -> {
                    sb.append('\\')
                    sb.append(ch)
                    escape = false
                }
                else -> {
                    escape = false
                    sb.append(ch)
                }
            }
        }
        if (star || doubleStar) {
            if (StringUtil.endsWithChar(sb, '/')) {
                sb.append(".+")
            } else {
                sb.append("[^/]*/?")
            }
        } else {
            if (StringUtil.endsWithChar(sb, '/')) {
                if (acceptChildren) {
                    sb.append("[^/]*")
                }
            } else {
                sb.append(if (acceptChildren) "(?:/.*)?" else "/?")
            }
        }
        sb.append('$')
        GLOBS_CACHE[it] = sb.toString()
        return sb.toString()
    }

    /** Clears [Glob.GLOBS_CACHE] cache.  */
    fun clearCache() {
        GLOBS_CACHE.clear()
        PATTERNS_CACHE.clear()
    }
}
