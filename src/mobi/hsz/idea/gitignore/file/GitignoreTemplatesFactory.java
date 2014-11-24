/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.file;

import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.GitignoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;

/**
 * Templates factory that generates Gitignore file and its content.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.1
 */
public class GitignoreTemplatesFactory implements FileTemplateGroupDescriptorFactory {
    /** File's content header. */
    private static final String TEXT = GitignoreBundle.message("file.templateNote");

    /** Group descriptor. */
    private final FileTemplateGroupDescriptor templateGroup;

    /** Builds a new instance of {@link GitignoreTemplatesFactory}. */
    public GitignoreTemplatesFactory() {
        templateGroup = new FileTemplateGroupDescriptor(GitignoreLanguage.NAME, Icons.FILE);
        templateGroup.addTemplate(GitignoreLanguage.FILENAME);
    }

    /**
     * Returns group descriptor.
     *
     * @return descriptor
     */
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        return templateGroup;
    }

    /**
     * Creates new Gitignore file or uses an existing one.
     *
     * @param directory working directory
     * @return file
     * @throws IncorrectOperationException
     */
    public static PsiFile createFromTemplate(final PsiDirectory directory) throws IncorrectOperationException {
        final PsiFile currentFile = directory.findFile(GitignoreLanguage.FILENAME);
        if (currentFile != null) {
            return currentFile;
        }
        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
        final PsiFile file = factory.createFileFromText(GitignoreLanguage.FILENAME, GitignoreFileType.INSTANCE, TEXT);
        return (PsiFile) directory.add(file);
    }
}
