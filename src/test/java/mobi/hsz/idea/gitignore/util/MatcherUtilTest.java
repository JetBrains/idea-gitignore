/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.util;

import mobi.hsz.idea.gitignore.Common;
import org.junit.Test;

import java.util.regex.Pattern;

public class MatcherUtilTest extends Common<MatcherUtil> {

    @Test
    public void testMatch() {
        final Pattern pattern = Pattern.compile("foo");
        final MatcherUtil util = new MatcherUtil();

        assertFalse(util.match(null, null));
        assertFalse(util.match(null, "foo"));
        assertFalse(util.match(pattern, null));
        assertFalse(util.match(pattern, "fo"));
        assertTrue(util.match(pattern, "foo"));
        assertTrue(util.match(pattern, "xfooy"));
    }

    @Test
    public void testMatchAllParts() {
        final String[] partsA = new String[]{"foo"};
        final String[] partsB = new String[]{"foo", "bar"};

        assertFalse(MatcherUtil.matchAllParts(null, null));
        assertFalse(MatcherUtil.matchAllParts(null, "foo"));
        assertFalse(MatcherUtil.matchAllParts(partsA, null));
        assertFalse(MatcherUtil.matchAllParts(partsA, "fo"));
        assertTrue(MatcherUtil.matchAllParts(partsA, "foo"));
        assertTrue(MatcherUtil.matchAllParts(partsA, "xfooy"));
        assertFalse(MatcherUtil.matchAllParts(partsB, "xfooxba"));
        assertTrue(MatcherUtil.matchAllParts(partsB, "xfooxbar"));
    }

    @Test
    public void testMatchAnyPart() {
        final String[] partsA = new String[]{"foo"};
        final String[] partsB = new String[]{"foo", "bar"};

        assertFalse(MatcherUtil.matchAnyPart(null, null));
        assertFalse(MatcherUtil.matchAnyPart(null, "foo"));
        assertFalse(MatcherUtil.matchAnyPart(partsA, null));
        assertFalse(MatcherUtil.matchAnyPart(partsA, "fo"));
        assertTrue(MatcherUtil.matchAnyPart(partsA, "foo"));
        assertTrue(MatcherUtil.matchAnyPart(partsA, "xfooy"));
        assertTrue(MatcherUtil.matchAnyPart(partsB, "xfooxba"));
        assertTrue(MatcherUtil.matchAnyPart(partsB, "xfooxbar"));
    }

    @Test
    public void testGetParts() {
        Pattern pattern = Pattern.compile("foo[ba]rbuz.*hi");

        assertEquals(MatcherUtil.getParts(null).length, 0);

        assertEquals(MatcherUtil.getParts(pattern).length, 2);

        pattern = Pattern.compile("$$_!@[fd]");
        assertEquals(MatcherUtil.getParts(pattern).length, 0);
    }
}
