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

package mobi.hsz.idea.gitignore.command;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.file.IgnoreTemplatesFactory;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Command action that creates new file in given directory.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.3.3
 */
public class CreateFileCommandAction extends WriteCommandAction<PsiFile> {
    /** Working directory. */
    private final PsiDirectory directory;

    /** Working file type. */
    private final IgnoreFileType fileType;

    /**
     * Builds a new instance of {@link CreateFileCommandAction}.
     *
     * @param project   current project
     * @param directory working directory
     * @param fileType  working file type
     */
    public CreateFileCommandAction(@NotNull Project project,
                                   @NotNull PsiDirectory directory,
                                   @NotNull IgnoreFileType fileType) {
        super(project);
        this.directory = directory;
        this.fileType = fileType;
    }

    /**
     * Creates a new file using {@link IgnoreTemplatesFactory#createFromTemplate(PsiDirectory)} to fill it with content.
     *
     * @param result command result
     * @throws Throwable
     */
    @Override
    protected void run(@NotNull Result<PsiFile> result) throws Throwable {
        IgnoreTemplatesFactory factory = new IgnoreTemplatesFactory(fileType);
        result.setResult(factory.createFromTemplate(directory));
    }
}
