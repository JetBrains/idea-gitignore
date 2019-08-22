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

package mobi.hsz.idea.gitignore.indexing;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.psi.IgnoreEntry;
import mobi.hsz.idea.gitignore.psi.IgnoreFile;
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor;
import mobi.hsz.idea.gitignore.util.Glob;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AbstractIgnoreFilesIndex} that allows to index all ignore files content using native
 * IDE mechanisms and increase indexing performance.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public class IgnoreFilesIndex extends AbstractIgnoreFilesIndex<IgnoreFileTypeKey, IgnoreEntryOccurrence> {
    /** Indexer ID. */
    public static final ID<IgnoreFileTypeKey, IgnoreEntryOccurrence> KEY = ID.create("IgnoreFilesIndex");

    /** Current indexer version. Has to be increased if significant changes have been done. */
    private static final int VERSION = 5;

    /**
     * Returns indexer's name.
     *
     * @return {@link #KEY}
     */
    @NotNull
    @Override
    public ID<IgnoreFileTypeKey, IgnoreEntryOccurrence> getName() {
        return KEY;
    }

    /**
     * Maps indexed files content to the {@link IgnoreEntryOccurrence}.
     *
     * @param inputData indexed file data
     * @return {@link IgnoreEntryOccurrence} data mapped with {@link IgnoreFileTypeKey}
     */
    @NotNull
    @Override
    public Map<IgnoreFileTypeKey, IgnoreEntryOccurrence> map(@NotNull final FileContent inputData) {
        PsiFile inputDataPsi;
        try {
            inputDataPsi = inputData.getPsiFile();
        } catch (Exception e) {
            // if there is some stale indices (e.g. for mobi.hsz.idea.gitignore.lang.kind.GitLanguage)
            // inputData.getPsiFile() could throw exception that should be avoided
            return Collections.emptyMap();
        }

        if (!(inputDataPsi instanceof IgnoreFile)) {
            return Collections.emptyMap();
        }

        final ArrayList<Pair<String, Boolean>> items = ContainerUtil.newArrayList();
        inputDataPsi.acceptChildren(new IgnoreVisitor() {
            @Override
            public void visitEntry(@NotNull IgnoreEntry entry) {
                final String regex = Glob.getRegex(entry.getValue(), entry.getSyntax(), false);
                items.add(Pair.create(regex, entry.isNegated()));
            }
        });

        return Collections.singletonMap(
                new IgnoreFileTypeKey((IgnoreFileType) inputData.getFileType()),
                new IgnoreEntryOccurrence(inputData.getFile().getUrl(), items)
        );
    }

    /**
     * Saves data to the indexing output stream.
     *
     * @param out   output stream
     * @param value filetype to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void save(@NotNull DataOutput out, IgnoreFileTypeKey value) throws IOException {
        out.writeUTF(value.getType().getLanguageName());
    }

    /**
     * Reads data from the input stream.
     *
     * @param in input stream
     * @return {@link IgnoreFileTypeKey} instance read from the stream
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized IgnoreFileTypeKey read(@NotNull DataInput in) throws IOException {
        final String languageName = in.readUTF();
        for (IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
            final IgnoreFileType type = language.getFileType();
            if (type.getLanguageName().equals(languageName)) {
                return new IgnoreFileTypeKey(type);
            }
        }
        return null;
    }

    /**
     * Returns {@link DataExternalizer} instance.
     *
     * @return {@link #DATA_EXTERNALIZER}
     */
    @NotNull
    @Override
    public DataExternalizer<IgnoreEntryOccurrence> getValueExternalizer() {
        return DATA_EXTERNALIZER;
    }

    /** {@link DataExternalizer} instance. */
    private static final DataExternalizer<IgnoreEntryOccurrence> DATA_EXTERNALIZER =
            new DataExternalizer<IgnoreEntryOccurrence>() {
                /**
                 * Saves data in the output stream.
                 *
                 * @param out output stream
                 * @param entry entry to write
                 * @throws IOException if an I/O error occurs
                 */
                @Override
                public void save(@NotNull DataOutput out, IgnoreEntryOccurrence entry) throws IOException {
                    IgnoreEntryOccurrence.serialize(out, entry);
                }

                /**
                 * Reads {@link IgnoreEntryOccurrence} from the input stream.
                 *
                 * @param in input stream
                 * @return read entry
                 */
                @Override
                public IgnoreEntryOccurrence read(@NotNull DataInput in) throws IOException {
                    return IgnoreEntryOccurrence.deserialize(in);
                }
            };

    /**
     * Returns current indexer {@link #VERSION}.
     *
     * @return current version
     */
    @Override
    public int getVersion() {
        return VERSION;
    }

    /**
     * Obtains if given {@link VirtualFile} is accepted by indexer.
     *
     * @param file to check
     * @return file is accepted
     */
    @Override
    public boolean acceptInput(@NotNull VirtualFile file) {
        return file.getFileType() instanceof IgnoreFileType ||
                IgnoreManager.FILE_TYPES_ASSOCIATION_QUEUE.containsKey(file.getName());
    }

    /**
     * Returns collection of indexed {@link IgnoreEntryOccurrence} for given {@link Project} and {@link IgnoreFileType}.
     *
     * @param project  current project
     * @param fileType filetype
     * @return {@link IgnoreEntryOccurrence} collection
     */
    @NotNull
    public static List<IgnoreEntryOccurrence> getEntries(@NotNull Project project, @NotNull IgnoreFileType fileType) {
        try {
            if (ApplicationManager.getApplication().isReadAccessAllowed()) {
                final GlobalSearchScope scope = IgnoreSearchScope.get(project);
                return FileBasedIndex.getInstance()
                        .getValues(IgnoreFilesIndex.KEY, new IgnoreFileTypeKey(fileType), scope);
            }
        } catch (RuntimeException ignored) {
        }
        return ContainerUtil.emptyList();
    }

    /**
     * Returns collection of indexed {@link VirtualFile} for given {@link Project} and {@link IgnoreFileTypeKey}.
     *
     * @param project  current project
     * @param fileType filetype
     * @return {@link VirtualFile} collection
     */
    @NotNull
    public static List<VirtualFile> getFiles(@NotNull Project project, @NotNull IgnoreFileType fileType) {
        return ContainerUtil.mapNotNull(getEntries(project, fileType), IgnoreEntryOccurrence::getFile);
    }
}
