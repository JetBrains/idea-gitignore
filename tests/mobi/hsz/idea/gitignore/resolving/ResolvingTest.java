package mobi.hsz.idea.gitignore.resolving;

import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.file.GitignoreFileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class ResolvingTest extends LightPlatformCodeInsightFixtureTestCase {
    public void testSimple() {
        myFixture.getTempDirFixture().createFile("fileName.txt");
        doTest("fileNa<caret>me.txt", "fileName.txt");
    }

    public void testNestedDirectory() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("dir").createChildData(this, "fileName.txt");
        doTest("dir/file<caret>Name.txt", "dir/fileName.txt");
    }

    public void testInHiddenDirectory() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir(".hidden").createChildData(this, "fileName.txt");
        doTest(".hidden/file<caret>Name.txt", ".hidden/fileName.txt");
    }

    public void testGlob() throws IOException {
        VirtualFile dir = myFixture.getTempDirFixture().findOrCreateDir("glob");
        dir.createChildData(this, "fileName1.txt");
        dir.createChildData(this, "fileName2.txt");
        doTest("glob/*<caret>.txt", "glob/fileName1.txt", "glob/fileName2.txt");
    }

    public void testGlobInParent() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("glob1").createChildData(this, "fileName.txt");
        myFixture.getTempDirFixture().findOrCreateDir("glob2").createChildData(this, "fileName.txt");
        doTest("*/file<caret>Name.txt", "glob1/fileName.txt", "glob2/fileName.txt");
    }

    public void testInvalidRegex() throws IOException {
        myFixture.getTempDirFixture().findOrCreateDir("glob").createChildData(this, "fileName1.txt");
        doTest("glob/fileN(<caret>.txt");
    }

    public void testNegation() throws IOException {
        myFixture.getTempDirFixture().createFile("fileName.txt");
        doTest("!fileNa<caret>me.txt", "fileName.txt");
    }
    
    private void doTest(@NotNull String beforeText, String... expectedResolve) {
        myFixture.configureByText(GitignoreFileType.INSTANCE, beforeText);
        PsiPolyVariantReference reference = ((PsiPolyVariantReference) myFixture.getReferenceAtCaretPosition());
        assertNotNull(reference);

        final VirtualFile rootFile = myFixture.getFile().getContainingDirectory().getVirtualFile();
        ResolveResult[] resolveResults = reference.multiResolve(true);
        List<String> actualResolve = ContainerUtil.map(resolveResults, new Function<ResolveResult, String>() {
            @Override
            public String fun(ResolveResult resolveResult) {
                PsiElement resolveResultElement = resolveResult.getElement();
                assertNotNull(resolveResultElement);
                assertInstanceOf(resolveResultElement, PsiFileSystemItem.class);
                PsiFileSystemItem fileSystemItem = (PsiFileSystemItem) resolveResultElement;
                return VfsUtilCore.getRelativePath(fileSystemItem.getVirtualFile(), rootFile, '/');
            }
        });

        assertContainsElements(actualResolve, expectedResolve);
    }

}
