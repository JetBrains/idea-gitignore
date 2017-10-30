/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
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
    private final String path;

    /** Current ignore file. */
    @Nullable
    private VirtualFile file;

    /** Collection of ignore entries converted to {@link Pattern}. */
    @NotNull
    private final List<Pair<Matcher, Boolean>> items = ContainerUtil.newArrayList();

    /**
     * Constructor.
     *
     * @param path current file
     */
    public IgnoreEntryOccurrence(@NotNull String path) {
        this(path, null);
    }

    /**
     * Constructor.
     *
     * @param file current file
     */
    public IgnoreEntryOccurrence(@NotNull VirtualFile file) {
        this(file.getPath(), file);
    }

    /**
     * Constructor.
     *
     * @param file current file
     * @param path current file path
     */
    public IgnoreEntryOccurrence(@NotNull String path, @Nullable VirtualFile file) {
        this.path = path;
        this.file = file;
    }

    /**
     * Calculates hashCode with {@link #file} and {@link #items} hashCodes.
     *
     * @return entry hashCode
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(path).append(items.toString()).toHashCode();
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
        boolean equals = path.equals(entry.path) && items.size() == entry.items.size();
        for (int i = 0; i < items.size(); i++) {
            equals = equals && items.get(i).toString().equals(entry.items.get(i).toString());
        }

        return equals;
    }

    /**
     * Returns current file path.
     *
     * @return current file path
     */
    @NotNull
    public String getPath() {
        return path;
    }

    /**
     * Returns current {@link VirtualFile}.
     *
     * @return current file
     */
    @Nullable
    public VirtualFile getFile() {
        if (file == null) {
            file = VirtualFileManager.getInstance().findFileByUrl(path);
        }
        return file;
    }

    /**
     * Returns entries for current file.
     *
     * @return entries
     */
    @NotNull
    public List<Pair<Matcher, Boolean>> getItems() {
        return items;
    }

    /**
     * Adds new element to {@link #items}.
     *
     * @param matcher   entry converted to {@link Matcher}
     * @param isNegated entry is negated
     */
    public void add(@NotNull Matcher matcher, boolean isNegated) {
        items.add(Pair.create(matcher, isNegated));
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
        out.writeUTF(entry.path);
        out.writeInt(entry.items.size());
        for (Pair<Matcher, Boolean> item : entry.items) {
            out.writeUTF(item.first.pattern().pattern());
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
    public static synchronized IgnoreEntryOccurrence deserialize(@NotNull DataInput in) {
        try {
            final String path = in.readUTF();
            if (StringUtils.isEmpty(path)) {
                return null;
            }

            final IgnoreEntryOccurrence entry = new IgnoreEntryOccurrence(path);
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                final Pattern pattern = Pattern.compile(in.readUTF());
                Boolean isNegated = in.readBoolean();
                entry.add(pattern.matcher(""), isNegated);
            }

            return entry;
        } catch (IOException ignored) {
        }

        return null;
    }
}
