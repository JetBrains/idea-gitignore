package mobi.hsz.idea.gitignore.treeviewfilter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Icons;

import javax.swing.*;

/**
 * Created by maxi on 03/01/17.
 */
public class ShowHideIgnoredFilesAction extends AnAction {

    private static Icon getIcon() {
        if (IgnoreSettings.getInstance().shouldHideIgnoredFilesOnProjectView()) {
            return Icons.IGNORE;
        }
        else {
            return null;
        }
    }

    public ShowHideIgnoredFilesAction() {
        super(
                IgnoreBundle.message("action.hideIgnoredVisibility"),
                IgnoreBundle.message("action.hideIgnoredVisibility.description"),
                getIcon()
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IgnoreSettings.getInstance().toggleIgnoredFilesOnProjectViewVisibility();

        Presentation presentation = this.getTemplatePresentation();
        presentation.setIcon(getIcon());
    }

}
