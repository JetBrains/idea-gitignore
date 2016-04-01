/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;

import javax.swing.*;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.3.2
 */
public class RefreshProgress extends ProgressIndicatorBase {
    private final String myMessage;

    public RefreshProgress(final String message) {
        myMessage = message;
    }

    @Override
    public void start() {
        super.start();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (ApplicationManager.getApplication().isDisposed()) {
                    return;
                }
                final WindowManager windowManager = WindowManager.getInstance();
                if (windowManager == null) {
                    return;
                }

                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                if (projects.length == 0) {
                    projects = new Project[]{null};
                }

                for (Project project : projects) {
                    final StatusBarEx statusBar = (StatusBarEx) windowManager.getStatusBar(project);
                    if (statusBar == null) {
                        continue;
                    }

                    statusBar.startRefreshIndication(myMessage);
                }
            }
        });
    }

    @Override
    public void stop() {
        super.stop();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (ApplicationManager.getApplication().isDisposed()) {
                    return;
                }
                final WindowManager windowManager = WindowManager.getInstance();
                if (windowManager == null) {
                    return;
                }

                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                if (projects.length == 0) {
                    projects = new Project[]{null};
                }

                for (Project project : projects) {
                    final StatusBarEx statusBar = (StatusBarEx) windowManager.getStatusBar(project);
                    if (statusBar == null) {
                        continue;
                    }

                    statusBar.stopRefreshIndication();
                }
            }
        });
    }
}
