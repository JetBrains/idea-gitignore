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

package mobi.hsz.idea.gitignore.vcs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusFactory;
import com.intellij.openapi.vcs.impl.FileStatusProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ThreeState;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Ignore instance of {@link FileStatusProvider} that provides {@link #ignoredStatus}
 * for the files matched with ignore rules.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.10
 */
public class IgnoreFileStatusProvider implements FileStatusProvider {

    /** Default ignored color. */
    public static final Color DEFAULT_COLOR = JBColor.GRAY;

    /** Ignored status key. */
    private static final String IGNORED_STATUS_KEY = "IGNORE.PROJECT_VIEW.IGNORED";

    /** Instance of {@link IgnoreManager}. */
    private final IgnoreManager ignoreManager;

    /** Ignored status. */
    private FileStatus ignoredStatus;

    /** Constructor. */
    public IgnoreFileStatusProvider(Project project) {
        this.ignoreManager = IgnoreManager.getInstance(project);

        IgnoreSettings settings = IgnoreSettings.getInstance();
        this.ignoredStatus = FileStatusFactory.getInstance().createFileStatus(
                IGNORED_STATUS_KEY,
                IgnoreBundle.message("projectView.ignored"),
                settings.getIgnoredColor()
        );
    }

    /**
     * Returns the {@link #ignoredStatus} status if file is ignored or <code>null</code>.
     *
     * @param virtualFile file to check
     * @return {@link #ignoredStatus} status or <code>null</code>
     */
    @Nullable
    @Override
    public FileStatus getFileStatus(@NotNull VirtualFile virtualFile) {
        return ignoreManager.isFileIgnored(virtualFile) ? ignoredStatus : null;
    }

    /** Does nothing. */
    @Override
    public void refreshFileStatusFromDocument(@NotNull VirtualFile virtualFile, @NotNull Document doc) {
    }

    /**
     * Does nothing.
     *
     * @param virtualFile file
     * @return nothing
     */
    @NotNull
    @Override
    public ThreeState getNotChangedDirectoryParentingStatus(@NotNull VirtualFile virtualFile) {
        throw new UnsupportedOperationException("Shouldn't be called");
    }
}
