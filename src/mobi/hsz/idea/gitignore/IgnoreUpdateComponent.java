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

package mobi.hsz.idea.gitignore;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import mobi.hsz.idea.gitignore.util.Notify;
import org.jetbrains.annotations.NotNull;

/**
 * ProjectComponent instance to display plugin's update information.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.3
 */
public class IgnoreUpdateComponent implements ProjectComponent {
    /** Current project. */
    private final Project project;

    /** {@link IgnoreApplicationComponent} instance. */
    private IgnoreApplicationComponent application;

    /**
     * Constructor.
     *
     * @param project current project
     */
    protected IgnoreUpdateComponent(@NotNull Project project) {
        this.project = project;
    }

    /** Component initialization method. */
    @Override
    public void initComponent() {
        application = IgnoreApplicationComponent.getInstance();
    }

    /** Component dispose method. */
    @Override
    public void disposeComponent() {
        application = null;
    }

    /**
     * Returns component's name.
     *
     * @return component's name
     */
    @NotNull
    @Override
    public String getComponentName() {
        return "IgnoreUpdateComponent";
    }

    /** Method called when project is opened. */
    @Override
    public void projectOpened() {
        if (application.isUpdated() && !application.isRC() && !application.isUpdateNotificationShown()) {
            application.setUpdateNotificationShown(true);
            Notify.showUpdate(project);
        }
    }
}
