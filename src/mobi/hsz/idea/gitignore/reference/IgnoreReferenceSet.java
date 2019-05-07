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

package mobi.hsz.idea.gitignore.reference;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.FilesIndexCacheProjectComponent;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.util.Constants;
import mobi.hsz.idea.gitignore.util.Glob;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * {@link FileReferenceSet} definition class.
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
public class IgnoreReferenceSet extends FileReferenceSet {
    /**
     * Instance of the Cache ProjectComponent that retrieves matching files using given {@link Pattern}.
     */
    @NotNull
    private final FilesIndexCacheProjectComponent filesIndexCache;

    /**
     * Instance of {@link IgnoreManager}.
     */
    @NotNull
    private final IgnoreManager manager;

    /**
     * Constructor.
     */
    public IgnoreReferenceSet(@NotNull IgnoreEntry element) {
        super(element);
        filesIndexCache = FilesIndexCacheProjectComponent.getInstance(element.getProject());
        manager = IgnoreManager.getInstance(element.getProject());
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
        return containingDirectory != null ? Collections.singletonList(containingDirectory) :
                super.computeDefaultContexts();
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
            return this.myReferences != null && this.myReferences.length > 1 ?
                    this.myReferences[this.myReferences.length - 2] : null;
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
        ProgressManager.checkCanceled();
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

        if (currentSlash + sepLen + sepLen < str.length() && str.substring(currentSlash + sepLen,
                currentSlash + sepLen + sepLen).equals(separatorString)) {
            currentSlash += sepLen;
        }
        int index = 0;

        if (str.equals(separatorString)) {
            final FileReference fileReference = createFileReference(
                    new TextRange(startInElement, startInElement + sepLen),
                    index++,
                    separatorString
            );
            referencesList.add(fileReference);
        }

        while (true) {
            ProgressManager.checkCanceled();
            final int nextSlash = str.indexOf(separatorString, currentSlash + sepLen);
            final String subReferenceText = nextSlash > 0 ? str.substring(0, nextSlash) : str;
            TextRange range = new TextRange(startInElement + currentSlash + sepLen, startInElement +
                    (nextSlash > 0 ? nextSlash : str.length()));
            final FileReference ref = createFileReference(range, index++, subReferenceText);
            referencesList.add(ref);
            if ((currentSlash = nextSlash) < 0) {
                break;
            }
        }

        myReferences = referencesList.toArray(new FileReference[0]);
    }

    /**
     * Custom definition of {@link FileReference}.
     */
    private class IgnoreReference extends FileReference {
        /**
         * Concurrent cache map.
         */
        private final ConcurrentMap<String, Collection<VirtualFile>> cacheMap;

        /**
         * Builds an instance of {@link IgnoreReferenceSet.IgnoreReference}.
         */
        public IgnoreReference(@NotNull FileReferenceSet fileReferenceSet, TextRange range, int index, String text) {
            super(fileReferenceSet, range, index, text);
            cacheMap = ContainerUtil.newConcurrentMap();
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
        protected void innerResolveInContext(@NotNull String text, @NotNull PsiFileSystemItem context,
                                             @NotNull final Collection<ResolveResult> result, boolean caseSensitive) {
            ProgressManager.checkCanceled();
            super.innerResolveInContext(text, context, result, caseSensitive);

            final PsiFile containingFile = getContainingFile();
            if (!(containingFile instanceof IgnoreFile)) {
                return;
            }

            VirtualFile contextVirtualFile;
            boolean isOuterFile = isOuterFile((IgnoreFile) containingFile);
            if (isOuterFile) {
                contextVirtualFile = Utils.getModuleRootForFile(
                        containingFile.getVirtualFile(),
                        containingFile.getProject()
                );
                result.clear();
            } else if (Utils.isInProject(containingFile.getVirtualFile(), getElement().getProject())) {
                contextVirtualFile = context.getVirtualFile();
            } else {
                return;
            }

            if (contextVirtualFile != null) {
                final IgnoreEntry entry = (IgnoreEntry) getFileReferenceSet().getElement();
                final String current = getCanonicalText();
                final Pattern pattern = Glob.createPattern(current, entry.getSyntax());
                if (pattern != null) {
                    PsiDirectory parent = getElement().getContainingFile().getParent();
                    final VirtualFile root = isOuterFile ? contextVirtualFile : ((parent != null) ?
                            parent.getVirtualFile() : null);
                    final PsiManager psiManager = getElement().getManager();

                    final List<VirtualFile> files = ContainerUtil.createLockFreeCopyOnWriteList();
                    files.addAll(filesIndexCache.getFilesForPattern(context.getProject(), pattern));
                    if (files.isEmpty()) {
                        files.addAll(ContainerUtil.newArrayList(context.getVirtualFile().getChildren()));
                    } else if (current.endsWith(Constants.STAR) && !current.equals(entry.getText())) {
                        files.addAll(ContainerUtil.filter(
                                context.getVirtualFile().getChildren(),
                                VirtualFile::isDirectory
                        ));
                    } else if (current.endsWith(Constants.DOUBLESTAR)) {
                        final String key = entry.getText();
                        if (!cacheMap.containsKey(key)) {
                            final Collection<VirtualFile> children = ContainerUtil.newArrayList();
                            final VirtualFileVisitor<?> visitor = new VirtualFileVisitor<Object>() {
                                @Override
                                public boolean visitFile(@NotNull VirtualFile file) {
                                    if (file.isDirectory()) {
                                        children.add(file);
                                        return true;
                                    }
                                    return false;
                                }
                            };

                            for (VirtualFile file : files) {
                                ProgressManager.checkCanceled();
                                if (!file.isDirectory()) {
                                    continue;
                                }
                                VfsUtil.visitChildrenRecursively(file, visitor);
                                children.remove(file);
                            }
                            cacheMap.put(key, children);

                        }
                        files.clear();
                        files.addAll(cacheMap.get(key));
                    }
                    for (VirtualFile file : files) {
                        ProgressManager.checkCanceled();
                        if (Utils.isVcsDirectory(file)) {
                            continue;
                        }

                        final String name = (root != null) ? Utils.getRelativePath(root, file) : file.getName();
                        if (manager.getMatcher().match(pattern, name)) {
                            PsiFileSystemItem psiFileSystemItem = getPsiFileSystemItem(psiManager, file);
                            if (psiFileSystemItem == null) {
                                continue;
                            }
                            result.add(new PsiElementResolveResult(psiFileSystemItem));
                        }
                    }
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
            if (!file.isValid()) {
                return null;
            }
            return file.isDirectory() ? manager.findDirectory(file) : manager.findFile(file);
        }
    }
}
