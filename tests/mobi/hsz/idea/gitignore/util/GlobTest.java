package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.Common;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.kind.GitFileType;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Pattern;

public class GlobTest extends Common<Glob> {

    @Test
    public void testPrivateConstructor() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        privateConstructor(Glob.class);
    }

    @Test
    public void testFind() {
        myFixture.configureByText(GitFileType.INSTANCE, createIgnoreContent("foo.txt", "bar.txt", "buz.txt", "vcsdir", "dir"));

        final VirtualFile rootFile = getFixtureRootFile();
        final List<IgnoreEntry> children = getFixtureChildrenEntries();
        List<VirtualFile> result;

        myFixture.addFileToProject("bar.txt", "bar content");
        myFixture.addFileToProject("buz.txt", "buz content");
        myFixture.addFileToProject("vcsdir/.git/info", "vcsdir git info content");
        myFixture.addFileToProject("dir/buz.txt", "buz2 content");
        myFixture.addFileToProject("dir/biz.txt", "buz2 content");

        VirtualFile dir = rootFile.findChild("dir");
        assertNotNull(dir);

        /** {@link Glob#find(VirtualFile, IgnoreEntry)} test */
        // foo.txt
        result = Glob.find(rootFile, children.get(0));
        assertNotNull(result);
        assertEmpty(result);

        // bar.txt
        result = Glob.find(rootFile, children.get(1));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(rootFile.findChild("bar.txt")));

        // buz.txt
        result = Glob.find(rootFile, children.get(2));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(rootFile.findChild("buz.txt")));
        assertTrue(result.contains(dir.findChild("buz.txt")));

        // ignore VCS directory
        result = Glob.find(rootFile, children.get(3));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(rootFile.findChild("vcsdir")));

        // dir
        result = Glob.find(rootFile, children.get(4));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(rootFile.findChild("dir")));

        /** {@link Glob#find(VirtualFile, IgnoreEntry, boolean)} test */
        // dir not includeNested
        result = Glob.find(rootFile, children.get(4), false);
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(rootFile.findChild("dir")));

        // dir includeNested
        result = Glob.find(rootFile, children.get(4), true);
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 3);
        assertTrue(result.contains(rootFile.findChild("dir")));
        assertTrue(result.contains(dir.findChild("buz.txt")));
        assertTrue(result.contains(dir.findChild("biz.txt")));
    }

    @Test
    public void testFindAsPaths() {
        myFixture.configureByText(GitFileType.INSTANCE, createIgnoreContent("foo.txt", "bar.txt", "buz.txt", "vcsdir", "dir"));

        final VirtualFile rootFile = getFixtureRootFile();
        final List<IgnoreEntry> children = getFixtureChildrenEntries();
        List<String> result;


        myFixture.addFileToProject("bar.txt", "bar content");
        myFixture.addFileToProject("buz.txt", "buz content");
        myFixture.addFileToProject("vcsdir/.git/info", "vcsdir git info content");
        myFixture.addFileToProject("dir/buz.txt", "buz2 content");
        myFixture.addFileToProject("dir/biz.txt", "buz2 content");

        final VirtualFile dir = rootFile.findChild("dir");
        assertNotNull(dir);

        /** {@link Glob#findAsPaths(VirtualFile, IgnoreEntry)} test */
        // foo.txt
        result = Glob.findAsPaths(rootFile, children.get(0));
        assertNotNull(result);
        assertEmpty(result);

        // bar.txt
        result = Glob.findAsPaths(rootFile, children.get(1));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 1);
        assertTrue(result.contains("bar.txt"));

        // buz.txt
        result = Glob.findAsPaths(rootFile, children.get(2));
        assertNotNull(result);
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        assertTrue(result.contains("buz.txt"));
        assertTrue(result.contains("dir/buz.txt"));
    }

    @Test
    public void testCreatePattern() throws Exception {
        Pattern pattern;

        pattern = Glob.createPattern("file.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("file.txt").matches());
        assertTrue(pattern.matcher("dir/file.txt").matches());
        assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("file1.txt").matches());
        Assert.assertFalse(pattern.matcher("otherfile.txt").matches());

        pattern = Glob.createPattern("file*.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("file.txt").matches());
        assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("fil[eE].txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("file.txt").matches());
        assertTrue(pattern.matcher("filE.txt").matches());
        Assert.assertFalse(pattern.matcher("fild.txt").matches());

        pattern = Glob.createPattern("dir/file.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("dir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("xdir/dir/file.txt").matches());
        Assert.assertFalse(pattern.matcher("xdir/file.txt").matches());

        pattern = Glob.createPattern("/file.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("file.txt").matches());
        Assert.assertFalse(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("fi**le.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("file.txt").matches());
        assertTrue(pattern.matcher("fi-foo-le.txt").matches());
        Assert.assertFalse(pattern.matcher("fi/le.txt").matches());
        Assert.assertFalse(pattern.matcher("fi/foo/le.txt").matches());

        pattern = Glob.createPattern("**/dir/file.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("foo/dir/file.txt").matches());
        assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("/dir/**/file.txt", IgnoreBundle.Syntax.GLOB);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        assertTrue(pattern.matcher("dir/subdir/foo/file.txt").matches());
        assertTrue(pattern.matcher("dir/file.txt").matches());

        pattern = Glob.createPattern("dir/*", IgnoreBundle.Syntax.GLOB, true);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("dir/file.txt").matches());
        assertTrue(pattern.matcher("dir/subdir/").matches());
        Assert.assertFalse(pattern.matcher("dir/").matches());

        pattern = Glob.createPattern("subdir", IgnoreBundle.Syntax.GLOB, true);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        assertTrue(pattern.matcher("dir/subdir/").matches());
        Assert.assertFalse(pattern.matcher("dir/foo/bar.txt").matches());

        pattern = Glob.createPattern("subdir/", IgnoreBundle.Syntax.GLOB, true);
        assertNotNull(pattern);
        assertTrue(pattern.matcher("dir/subdir/file.txt").matches());
        assertTrue(pattern.matcher("dir/subdir/").matches());
        Assert.assertFalse(pattern.matcher("dir/foo/bar.txt").matches());
    }

}