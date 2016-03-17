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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.labels.LinkListener;
import com.intellij.util.ui.UIUtil;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Wrapper that creates bottom editor component for displaying outer ignore rules.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.1
 */
public class OuterIgnoreWrapper implements Disposable {
    private static final int DRAG_OFFSET = 10;

    private final JPanel panel;
    private final Editor outerEditor;

    /** The settings storage object. */
    private final IgnoreSettings settings;

    private int dragScrollPanelHeight;
    private int dragYOnScreen;
    private boolean drag;

    public OuterIgnoreWrapper(@NotNull final Project project, @NotNull final VirtualFile outerFile) {
        settings = IgnoreSettings.getInstance();

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        JBLabel label = new JBLabel(IgnoreBundle.message("outer.label"), UIUtil.ComponentStyle.REGULAR, UIUtil.FontColor.BRIGHTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        final JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        northPanel.add(label);
        northPanel.add(new LinkLabel(outerFile.getPath(), null, new LinkListener() {
            @Override
            public void linkSelected(LinkLabel aSource, Object aLinkData) {
                Utils.openFile(project, outerFile);
            }
        }));

        Document document = FileDocumentManager.getInstance().getDocument(outerFile);
        outerEditor = document != null ? Utils.createPreviewEditor(document, project, true) : null;

        if (outerEditor != null) {
            final JScrollPane scrollPanel = ScrollPaneFactory.createScrollPane(outerEditor.getComponent());
            scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPanel.setPreferredSize(new Dimension(0, settings.getOuterIgnoreWrapperHeight()));

            northPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getPoint().getY() <= DRAG_OFFSET) {
                        dragScrollPanelHeight = scrollPanel.getHeight();
                        dragYOnScreen = e.getYOnScreen();
                        drag = true;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    drag = false;
                    settings.setOuterIgnoreWrapperHeight(scrollPanel.getHeight());
                }
            });
            northPanel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Cursor cursor = (e.getPoint().getY() <= DRAG_OFFSET) ? Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) : Cursor.getDefaultCursor();
                    panel.setCursor(cursor);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (drag) {
                        scrollPanel.setPreferredSize(new Dimension(0, dragScrollPanelHeight - e.getYOnScreen() + dragYOnScreen));
                        panel.revalidate();
                    }
                }
            });

            panel.add(northPanel, BorderLayout.NORTH);
            panel.add(scrollPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Returns outer panel.
     *
     * @return outer panel
     */
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void dispose() {
        if (outerEditor != null) {
            EditorFactory.getInstance().releaseEditor(outerEditor);
        }
    }
}
