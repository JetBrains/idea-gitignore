// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.components.service
import junit.framework.TestCase
import mobi.hsz.idea.gitignore.Common
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import org.junit.Test
import java.util.regex.Pattern

class MatcherUtilTest : Common<MatcherUtil?>() {

    @Test
    fun testMatch() {
        val pattern = Pattern.compile("foo")
        val matcher = project.service<IgnoreMatcher>()

        TestCase.assertFalse(matcher.match(null, null))
        TestCase.assertFalse(matcher.match(null, "foo"))
        TestCase.assertFalse(matcher.match(pattern, null))
        TestCase.assertFalse(matcher.match(pattern, "fo"))
        TestCase.assertTrue(matcher.match(pattern, "foo"))
        TestCase.assertTrue(matcher.match(pattern, "xfooy"))
    }

    @Test
    fun testMatchAllParts() {
        val partsA = arrayOf<String?>("foo")
        val partsB = arrayOf<String?>("foo", "bar")
        TestCase.assertFalse(MatcherUtil.matchAllParts(null, null))
        TestCase.assertFalse(MatcherUtil.matchAllParts(null, "foo"))
        TestCase.assertFalse(MatcherUtil.matchAllParts(partsA, null))
        TestCase.assertFalse(MatcherUtil.matchAllParts(partsA, "fo"))
        TestCase.assertTrue(MatcherUtil.matchAllParts(partsA, "foo"))
        TestCase.assertTrue(MatcherUtil.matchAllParts(partsA, "xfooy"))
        TestCase.assertFalse(MatcherUtil.matchAllParts(partsB, "xfooxba"))
        TestCase.assertTrue(MatcherUtil.matchAllParts(partsB, "xfooxbar"))
    }

    @Test
    fun testMatchAnyPart() {
        val partsA = arrayOf<String?>("foo")
        val partsB = arrayOf<String?>("foo", "bar")
        TestCase.assertFalse(MatcherUtil.matchAnyPart(null, null))
        TestCase.assertFalse(MatcherUtil.matchAnyPart(null, "foo"))
        TestCase.assertFalse(MatcherUtil.matchAnyPart(partsA, null))
        TestCase.assertFalse(MatcherUtil.matchAnyPart(partsA, "fo"))
        TestCase.assertTrue(MatcherUtil.matchAnyPart(partsA, "foo"))
        TestCase.assertTrue(MatcherUtil.matchAnyPart(partsA, "xfooy"))
        TestCase.assertTrue(MatcherUtil.matchAnyPart(partsB, "xfooxba"))
        TestCase.assertTrue(MatcherUtil.matchAnyPart(partsB, "xfooxbar"))
    }

    @Test
    fun testGetParts() {
        var pattern = Pattern.compile("foo[ba]rbuz.*hi")
        TestCase.assertEquals(MatcherUtil.getParts(null).size, 0)
        TestCase.assertEquals(MatcherUtil.getParts(pattern).size, 2)
        pattern = Pattern.compile("$$!@[fd]")
        TestCase.assertEquals(MatcherUtil.getParts(pattern).size, 0)
    }
}
