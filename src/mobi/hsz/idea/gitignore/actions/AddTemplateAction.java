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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.psi.GitignoreFile;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;
import mobi.hsz.idea.gitignore.util.Icons;

/**
 * Action that initiates adding new template to the selected .gitignore file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5.3
 */
public class AddTemplateAction extends AnAction {
    public AddTemplateAction() {
        super(GitignoreBundle.message("action.addTemplate"), GitignoreBundle.message("action.addTemplate.description"), Icons.FILE);
    }

    /**
     * Handles an action of adding new template.
     * Ignores action if selected file is not a {@link GitignoreFile} instance, otherwise shows {@link GeneratorDialog}.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if (file == null || !(file instanceof GitignoreFile)) {
            return;
        }

        new GeneratorDialog(project, file).show();
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     * Visible only for {@link GitignoreFile} context.
     *
     * @param e action event
     */
    @Override
    public void update(AnActionEvent e) {
        final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if (file == null || !(file instanceof GitignoreFile)) {
            e.getPresentation().setVisible(false);
        }
    }
}
