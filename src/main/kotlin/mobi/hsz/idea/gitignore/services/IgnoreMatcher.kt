package mobi.hsz.idea.gitignore.services

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.util.containers.IntObjectCache
import mobi.hsz.idea.gitignore.util.MatcherUtil
import java.util.*
import java.util.regex.Pattern

@Service(Service.Level.PROJECT)
class IgnoreMatcher : Disposable {

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
        } else {
            synchronized(cache) {
                val hashCode = Objects.hash(pattern, path)
                if (!cache.containsKey(hashCode)) {
                    val parts = MatcherUtil.getParts(pattern)
                    var result = false
                    if (parts.isEmpty() || MatcherUtil.matchAllParts(parts, path)) {
                        try {
                            result = pattern.matcher(path).find()
                        } catch (ignored: StringIndexOutOfBoundsException) {
                        }
                    }
                    cache.put(hashCode, result)
                }
                return cache[hashCode]
            }
        }
    }

    override fun dispose() {
        cache.removeAll()
    }
}
