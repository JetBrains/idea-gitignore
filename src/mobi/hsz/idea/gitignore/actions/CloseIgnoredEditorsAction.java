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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.ide.actions.CloseEditorsActionBase;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.impl.EditorComposite;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.vcs.IgnoreFileStatusProvider;

/**
 * Action that closes all opened files that are marked as {@link IgnoreFileStatusProvider#IGNORED}.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.2
 */
public class CloseIgnoredEditorsAction extends CloseEditorsActionBase {
    /**
     * Obtains if editor is allowed to be closed.
     *
     * @return editor is allowed to be closed
     */
    @Override
    protected boolean isFileToClose(final EditorComposite editor, final EditorWindow window) {
        final FileStatusManager fileStatusManager = FileStatusManager.getInstance(window.getManager().getProject());
        return fileStatusManager != null && fileStatusManager.getStatus(editor.getFile()).equals(IgnoreFileStatusProvider.IGNORED);
    }

    /**
     * Checks if action is available in UI.
     *
     * @return action is enabled
     */
    @Override
    protected boolean isActionEnabled(final Project project, final AnActionEvent event) {
        return super.isActionEnabled(project, event) && ProjectLevelVcsManager.getInstance(project).getAllActiveVcss().length > 0;
    }

    /**
     * Returns presentation text.
     *
     * @param inSplitter is in splitter
     * @return label text
     */
    @Override
    protected String getPresentationText(final boolean inSplitter) {
        return inSplitter ? IgnoreBundle.message("action.closeIgnored.editors.in.tab.group") : IgnoreBundle.message("action.closeIgnored.editors");
    }
}
