/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.util.Glob;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link FileReferenceSet} definition class.
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreReferenceSet extends FileReferenceSet {
    public IgnoreReferenceSet(@NotNull IgnoreEntry element) {
        super(element);
    }

    /**
     * Creates {@link IgnoreReference} instance basing on passed text value.
     *
     * @param range text range
     * @param index start index
     * @param text  string text
     * @return file reference
     */
    @Override
    public FileReference createFileReference(TextRange range, int index, String text) {
        return new IgnoreReference(this, range, index, text);
    }

    /**
     * Sets ending slash as allowed.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean isEndingSlashNotAllowed() {
        return false;
    }

    /**
     * Computes current element's parent context.
     *
     * @return contexts collection
     */
    @NotNull
    @Override
    public Collection<PsiFileSystemItem> computeDefaultContexts() {
        PsiFile containingFile = getElement().getContainingFile();
        PsiDirectory containingDirectory = containingFile.getParent();
        return containingDirectory != null ? Collections.<PsiFileSystemItem>singletonList(containingDirectory) : super.computeDefaultContexts();
    }

    /**
     * Returns last reference of the current element's references.
     *
     * @return last {@link FileReference}
     */
    @Nullable
    public FileReference getLastReference() {
        FileReference lastReference = super.getLastReference();
        if (lastReference != null && lastReference.getCanonicalText().endsWith(getSeparatorString())) {
            return this.myReferences != null && this.myReferences.length > 1 ? this.myReferences[this.myReferences.length - 2] : null;
        }
        return lastReference;
    }

    /**
     * Disallows conversion to relative reference.
     *
     * @param relative is ignored
     * @return <code>false</code>
     */
    @Override
    public boolean couldBeConvertedTo(boolean relative) {
        return false;
    }

    /**
     * Parses entry, searches for file references and stores them in {@link #myReferences}.
     */
    @Override
    protected void reparse() {
        String str = StringUtil.trimEnd(getPathString(), getSeparatorString());
        final List<FileReference> referencesList = ContainerUtil.newArrayList();

        String separatorString = getSeparatorString(); // separator's length can be more then 1 char
        int sepLen = separatorString.length();
        int currentSlash = -sepLen;
        int startInElement = getStartInElement();

        // skip white space
        while (currentSlash + sepLen < str.length() && Character.isWhitespace(str.charAt(currentSlash + sepLen))) {
            currentSlash++;
        }

        if (currentSlash + sepLen + sepLen < str.length() && str.substring(currentSlash + sepLen, currentSlash + sepLen + sepLen).equals(separatorString)) {
            currentSlash += sepLen;
        }
        int index = 0;

        if (str.equals(separatorString)) {
            final FileReference fileReference = createFileReference(new TextRange(startInElement, startInElement + sepLen), index++, separatorString);
            referencesList.add(fileReference);
        }

        while (true) {
            final int nextSlash = str.indexOf(separatorString, currentSlash + sepLen);
            final String subReferenceText = nextSlash > 0 ? str.substring(0, nextSlash) : str;
            TextRange range = new TextRange(startInElement + currentSlash + sepLen, startInElement + (nextSlash > 0 ? nextSlash : str.length()));
            final FileReference ref = createFileReference(range, index++, subReferenceText);
            referencesList.add(ref);
            if ((currentSlash = nextSlash) < 0) {
                break;
            }
        }

        myReferences = referencesList.toArray(new FileReference[referencesList.size()]);
    }

    /**
     * Custom definition of {@link FileReference}.
     */
    private class IgnoreReference extends FileReference {
        /**
         * Builds an instance of {@link IgnoreReferenceSet.IgnoreReference}.
         */
        public IgnoreReference(@NotNull FileReferenceSet fileReferenceSet, TextRange range, int index, String text) {
            super(fileReferenceSet, range, index, text);
        }

        /**
         * Resolves reference to the filesystem.
         *
         * @param text          entry
         * @param context       filesystem context
         * @param result        result references collection
         * @param caseSensitive is ignored
         */
        @Override
        protected void innerResolveInContext(@NotNull String text, @NotNull PsiFileSystemItem context, final Collection<ResolveResult> result, boolean caseSensitive) {
            super.innerResolveInContext(text, context, result, caseSensitive);
            VirtualFile contextVirtualFile;

            final PsiFile containingFile = getContainingFile();
            boolean isOuterFile = isOuterFile((IgnoreFile) containingFile);

            if (isOuterFile) {
                contextVirtualFile = getElement().getProject().getBaseDir();
                result.clear();
            } else if (containingFile != null && Utils.isInProject(containingFile.getVirtualFile(), getElement().getProject())) {
                contextVirtualFile = context.getVirtualFile();
            } else {
                return;
            }

            if (contextVirtualFile != null) {
                IgnoreEntry entry = (IgnoreEntry) getFileReferenceSet().getElement();
                final Pattern pattern = Glob.createPattern(getCanonicalText(), entry.getSyntax());
                if (pattern != null) {
                    PsiDirectory parent = getElement().getContainingFile().getParent();
                    final VirtualFile root = isOuterFile ? contextVirtualFile : ((parent != null) ? parent.getVirtualFile() : null);
                    final PsiManager manager = getElement().getManager();
                    final Matcher matcher = pattern.matcher("");

                    VirtualFileVisitor<?> fileVisitor = new VirtualFileVisitor(VirtualFileVisitor.NO_FOLLOW_SYMLINKS) {
                        @Override
                        public boolean visitFile(@NotNull VirtualFile file) {
                            if (Utils.isVcsDirectory(file)) {
                                return false;
                            }

                            String name = (root != null) ? Utils.getRelativePath(root, file) : file.getName();
                            if (Utils.match(matcher, name)) {
                                PsiFileSystemItem psiFileSystemItem = getPsiFileSystemItem(manager, file);
                                if (psiFileSystemItem == null) {
                                    return false;
                                }
                                result.add(new PsiElementResolveResult(psiFileSystemItem));
                            }
                            return true;
                        }
                    };
                    VfsUtil.visitChildrenRecursively(contextVirtualFile, fileVisitor);
                }
            }
        }

        /**
         * Checks if {@link IgnoreFile} is defined as an outer rules file.
         *
         * @param file current file
         * @return is outer file
         */
        private boolean isOuterFile(@Nullable IgnoreFile file) {
            return file != null && file.isOuter();
        }

        /**
         * Searches for directory or file using {@link PsiManager}.
         *
         * @param manager {@link PsiManager} instance
         * @param file    working file
         * @return Psi item
         */
        @Nullable
        private PsiFileSystemItem getPsiFileSystemItem(@NotNull PsiManager manager, @NotNull VirtualFile file) {
            return file.isDirectory() ? manager.findDirectory(file) : manager.findFile(file);
        }
    }
}
