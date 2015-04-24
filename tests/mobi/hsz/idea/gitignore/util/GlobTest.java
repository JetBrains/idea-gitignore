package mobi.hsz.idea.gitignore.util;

import mobi.hsz.idea.gitignore.IgnoreBundle;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class GlobTest {

    @Test
    public void testCreatePattern() throws Exception {
        Pattern pattern;

        pattern = Glob.createPattern("file.txt", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("file1.txt").matches());
        Assert.assertFalse(pattern.matcher("otherfile.txt").matches());

        pattern = Glob.createPattern("file*.txt",  IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("fil[eE].txt",  IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertTrue(pattern.matcher("filE.txt").matches());
        Assert.assertFalse(pattern.matcher("fild.txt").matches());

        pattern = Glob.createPattern("dir/file.txt",  IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("xdir/dir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("xdir/file.txt").matches());

        pattern = Glob.createPattern("/file.txt", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertFalse(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("fi**le.txt", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertTrue(pattern.matcher("fi-foo-le.txt").matches());
        Assert.assertFalse(pattern.matcher("fi/le.txt").matches());
        Assert.assertFalse(pattern.matcher("fi/foo/le.txt").matches());

        pattern = Glob.createPattern("**/dir/file.txt", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("foo/dir/file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("/dir/**/file.txt", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/subdir/foo/file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("dir/*", IgnoreBundle.Syntax.GLOB);
        Assert.assertTrue(pattern.matcher("dir/file.txt").matches());
        Assert.assertTrue(pattern.matcher("dir/subdir/").matches());
        Assert.assertFalse(pattern.matcher("dir/").matches());
    }

}