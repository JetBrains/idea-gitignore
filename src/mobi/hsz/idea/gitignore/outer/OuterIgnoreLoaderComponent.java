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

package mobi.hsz.idea.gitignore.outer;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Component loader for outer ignore files editor.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.1
 */
public class OuterIgnoreLoaderComponent extends AbstractProjectComponent {
    /** Current project. */
    private final Project project;

    /** Constructor. */
    public OuterIgnoreLoaderComponent(@NotNull final Project project) {
        super(project);
        this.project = project;
    }

    /**
     * Returns component name.
     *
     * @return component name
     */
    @Override
    @NonNls
    @NotNull
    public String getComponentName() {
        return "IgnoreOuterComponent";
    }

    /** Initializes component.*/
    @Override
    public void initComponent() {
        myProject.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new IgnoreEditorManagerListener(project));
    }

    /**
     * Checks if outer ignore rules are enabled.
     *
     * @return outer ignore rules enabled
     */
    private static boolean isEnabled() {
        return IgnoreSettings.getInstance().isOuterIgnoreRules();
    }

    /**
     * Listener for ignore editor manager.
     */
    private static class IgnoreEditorManagerListener extends FileEditorManagerAdapter {
        /** Current project. */
        private final Project project;

        /** Constructor. */
        public IgnoreEditorManagerListener(@NotNull final Project project) {
            this.project = project;
        }

        /**
         * Handles file opening event and attaches outer ignore component.
         *
         * @param source editor manager
         * @param file current file
         */
        @Override
        public void fileOpened(@NotNull final FileEditorManager source, @NotNull final VirtualFile file) {
            if (!isEnabled() || !(file.getFileType() instanceof IgnoreFileType)) {
                return;
            }

            VirtualFile outerFile = ((IgnoreFileType) file.getFileType()).getIgnoreLanguage().getOuterFile();

            if (outerFile == null || outerFile.equals(file)) {
                return;
            }

            for (final FileEditor fileEditor : source.getEditors(file)) {
                if (fileEditor instanceof TextEditor) {
                    final OuterIgnoreWrapper wrapper = new OuterIgnoreWrapper(project, outerFile);
                    final JComponent c = wrapper.getComponent();
                    source.addBottomComponent(fileEditor, c);

                    IgnoreSettings.getInstance().addListener(new IgnoreSettings.Listener() {
                        @Override
                        public void onChange(@NotNull String key, Object value) {
                            if ("outerIgnoreRules".equals(key)) {
                                c.setVisible((Boolean) value);
                            }
                        }
                    });

                    Disposer.register(fileEditor, wrapper);
                    Disposer.register(fileEditor, new Disposable() {
                        @Override
                        public void dispose() {
                            source.removeBottomComponent(fileEditor, c);
                        }
                    });
                }
            }
        }
    }
}