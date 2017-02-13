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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage;
import mobi.hsz.idea.gitignore.util.Icons;

/**
 * Creates a group of {@link NewFileAction} instances.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.9
 */
public class NewFileGroupAction extends DefaultActionGroup implements DumbAware {
    /** Builds a new instance of {@link NewFileGroupAction}. */
    @SuppressWarnings("checkstyle:whitespacearound")
    public NewFileGroupAction() {
        setPopup(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(IgnoreBundle.message("action.newFile.group"));
        presentation.setIcon(Icons.IGNORE);

        for (final IgnoreLanguage language : IgnoreBundle.LANGUAGES) {
            final IgnoreFileType fileType = language.getFileType();
            add(new NewFileAction(fileType) {{
                Presentation p = getTemplatePresentation();
                p.setText(IgnoreBundle.message("action.newFile", language.getFilename(), language.getDisplayName()));
                p.setDescription(IgnoreBundle.message("action.newFile.description", language.getDisplayName()));
                p.setIcon(fileType.getIcon());
            }});
        }
    }
}
