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

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Entry containing information about the {@link VirtualFile} instance of the ignore file mapped with the collection
 * of ignore entries converted to {@link Pattern} for better performance. Class is used for indexing.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
public class IgnoreEntryOccurrence implements Serializable {
    /** Current ignore file path. */
    @NotNull
    private final String url;

    /** Current ignore file. */
    @Nullable
    private VirtualFile file;

    /** Collection of ignore entries converted to {@link Pattern}. */
    @Nullable
    private Pair<Pattern, Boolean>[] items;

    /**
     * Constructor.
     *
     * @param url current file
     */
    public IgnoreEntryOccurrence(@NotNull String url) {
        this(url, null);
    }

    /**
     * Constructor.
     *
     * @param file current file
     */
    public IgnoreEntryOccurrence(@NotNull VirtualFile file) {
        this(file.getUrl(), file);
    }

    /**
     * Constructor.
     *
     * @param file current file
     * @param url current file path
     */
    public IgnoreEntryOccurrence(@NotNull String url, @Nullable VirtualFile file) {
        this.url = url;
        this.file = file;
    }

    /**
     * Calculates hashCode with {@link #file} and {@link #items} hashCodes.
     *
     * @return entry hashCode
     */
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(url);

        if (items != null) {
            for (Pair<Pattern, Boolean> item : items) {
                builder = builder.append(item.first).append(item.second);
            }
        }

        return builder.toHashCode();
    }

    /**
     * Checks if given object is equal to current {@link IgnoreEntryOccurrence} instance.
     *
     * @param obj to check
     * @return objects are equal.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof IgnoreEntryOccurrence)) {
            return false;
        }

        final IgnoreEntryOccurrence entry = (IgnoreEntryOccurrence) obj;
        if (items == null && entry.items == null) {
            return true;
        }
        if (items == null || entry.items == null) {
            return false;
        }

        boolean equals = url.equals(entry.url) && items.length == entry.items.length;
        for (int i = 0; i < items.length; i++) {
            equals = equals && items[i].toString().equals(entry.items[i].toString());
        }

        return equals;
    }

    /**
     * Returns current {@link VirtualFile}.
     *
     * @return current file
     */
    @Nullable
    public VirtualFile getFile() {
        if (file == null) {
            file = VirtualFileManager.getInstance().findFileByUrl(url);
        }
        return file;
    }

    /**
     * Returns entries for current file.
     *
     * @return entries
     */
    @Nullable
    public Pair<Pattern, Boolean>[] getItems() {
        return items;
    }

    /** Set entries for current file. */
    public void setItems(@Nullable Pair<Pattern, Boolean>[] items) {
        this.items = items;
    }

    /**
     * Static helper to write given {@link IgnoreEntryOccurrence} to the output stream.
     *
     * @param out   output stream
     * @param entry entry to write
     * @throws IOException I/O exception
     */
    public static synchronized void serialize(@NotNull DataOutput out, @NotNull IgnoreEntryOccurrence entry)
            throws IOException {
        out.writeUTF(entry.url);
        out.writeInt(entry.items == null ? 0 : entry.items.length);
        for (Pair<Pattern, Boolean> item : entry.items) {
            out.writeUTF(item.first.pattern());
            out.writeBoolean(item.second);
        }
    }

    /**
     * Static helper to read {@link IgnoreEntryOccurrence} from the input stream.
     *
     * @param in input stream
     * @return read {@link IgnoreEntryOccurrence}
     */
    @Nullable
    public static synchronized IgnoreEntryOccurrence deserialize(@NotNull DataInput in) throws IOException {
        final String url = in.readUTF();
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        final IgnoreEntryOccurrence entry = new IgnoreEntryOccurrence(url);
        int size = in.readInt();

        @SuppressWarnings("unchecked")
        Pair<Pattern, Boolean>[] items = (Pair<Pattern, Boolean>[]) new Pair[size];
        for (int i = 0; i < size; i++) {
            final Pattern pattern = Pattern.compile(in.readUTF());
            Boolean isNegated = in.readBoolean();
            items[i] = Pair.create(pattern, isNegated);
        }
        entry.setItems(items);

        return entry;
    }
}
