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

package mobi.hsz.idea.gitignore.lang.kind;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.file.type.kind.FossilFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.outer.OuterIgnoreLoaderComponent.OuterFileFetcher;
import mobi.hsz.idea.gitignore.util.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Fossil {@link IgnoreLanguage} definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.1
 */
public class FossilLanguage extends IgnoreLanguage {
    /** The {@link FossilLanguage} instance. */
    public static final FossilLanguage INSTANCE = new FossilLanguage();

    /** {@link IgnoreLanguage} is a non-instantiable static class. */
    private FossilLanguage() {
        super("Fossil", "ignore-glob", ".fossil-settings", Icons.FOSSIL, new OuterFileFetcher[]{

                // Outer file fetched from the .fossil-settings/ignore-glob file.
                project -> {
                    final VirtualFile baseDir = project.getBaseDir();
                    return ContainerUtil.createMaybeSingletonList(baseDir == null ? null : baseDir
                            .findFileByRelativePath(INSTANCE.getVcsDirectory() + "/" + INSTANCE.getFilename()));
                }

        });
    }

    /**
     * Language file type.
     *
     * @return {@link FossilFileType} instance
     */
    @NotNull
    @Override
    public IgnoreFileType getFileType() {
        return FossilFileType.INSTANCE;
    }

    /**
     * The Gitignore file extension.
     *
     * @return filename
     */
    @NotNull
    @Override
    public String getFilename() {
        return getExtension();
    }

    /**
     * Defines if {@link FossilLanguage} supports outer ignore files.
     *
     * @return supports outer ignore files
     */
    @Override
    public boolean isOuterFileSupported() {
        return true;
    }
}
