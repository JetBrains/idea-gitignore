package mobi.hsz.idea.gitignore.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import mobi.hsz.idea.gitignore.psi.GitignoreEntry;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class GitignoreReferenceSet extends FileReferenceSet {
    public GitignoreReferenceSet(@NotNull GitignoreEntry element) {
        super(element);
    }

    @Override
    public FileReference createFileReference(TextRange range, int index, String text) {
        return new GitReference(this, range, index, text);
    }

    @Override
    public boolean isEndingSlashNotAllowed() {
        return false;
    }

    @NotNull
    @Override
    public Collection<PsiFileSystemItem> computeDefaultContexts() {
        PsiFile containingFile = getElement().getContainingFile();
        PsiDirectory containingDirectory = containingFile.getParent();
        return containingDirectory != null ? Collections.<PsiFileSystemItem>singletonList(containingDirectory) : super.computeDefaultContexts();
    }

    @Nullable
    public FileReference getLastReference() {
        FileReference lastReference = super.getLastReference();
        if (lastReference != null && lastReference.getCanonicalText().isEmpty()) {
            return this.myReferences != null && this.myReferences.length > 1 ? this.myReferences[this.myReferences.length - 2] : null;
        }
        return lastReference;
    }

    @Override
    public boolean couldBeConvertedTo(boolean relative) {
        return false;
    }

    private class GitReference extends FileReference {
        public GitReference(@NotNull FileReferenceSet fileReferenceSet, TextRange range, int index, String text) {
            super(fileReferenceSet, range, index, text);
        }

        @Override
        protected void innerResolveInContext(@NotNull String text, @NotNull PsiFileSystemItem context, Collection<ResolveResult> result, boolean caseSensitive) {
            super.innerResolveInContext(text, context, result, caseSensitive);
            VirtualFile contextVirtualFile = context.getVirtualFile();
            if (contextVirtualFile != null) {
                Pattern pattern = Glob.createPattern(getCanonicalText());
                if (pattern != null) {
                    walk(result, pattern, contextVirtualFile);
                }
            }
        }

        private void walk(Collection<ResolveResult> result, Pattern pattern, VirtualFile directory) {
            PsiManager psiManager = getElement().getManager();
            for (VirtualFile file : directory.getChildren()) {
                if (pattern.matcher(file.getName()).matches()) {
                    PsiFileSystemItem psiFileSystemItem = FileReferenceHelper.getPsiFileSystemItem(psiManager, file);
                    if (psiFileSystemItem != null) {
                        result.add(new PsiElementResolveResult(psiFileSystemItem));
                    }
                }

                if (file.isDirectory() && !file.is(VFileProperty.SYMLINK)) {
                    walk(result, pattern, file);
                }
            }
        }
    }
}
