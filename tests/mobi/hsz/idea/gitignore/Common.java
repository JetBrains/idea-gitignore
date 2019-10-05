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

package mobi.hsz.idea.gitignore;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public abstract class Common<T> extends LightPlatformCodeInsightFixtureTestCase {

    protected void privateConstructor(@NotNull Class<T> clz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = clz.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @NotNull
    protected String createIgnoreContent(String... entries) {
        return StringUtil.join(entries, Constants.NEWLINE);
    }

    @NotNull
    protected VirtualFile getFixtureRootFile() {
        return myFixture.getFile().getContainingDirectory().getVirtualFile();
    }

    @NotNull
    protected List<IgnoreEntry> getFixtureChildrenEntries() {
        final List<IgnoreEntry> children = new ArrayList<>();
        myFixture.getFile().acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                children.add(entry);
            }
        });
        return children;
    }

}
