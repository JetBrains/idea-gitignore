package mobi.hsz.idea.gitignore.treeviewfilter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Icons;


/**
 * Created by maxi on 03/01/17.
 */
public class ShowHideIgnoredFilesAction extends AnAction {

    private static String getText() {
        if (IgnoreSettings.getInstance().shouldHideIgnoredFilesOnProjectView()) {
            return IgnoreBundle.message("action.showIgnoredVisibility");
        }
        else {
            return IgnoreBundle.message("action.hideIgnoredVisibility");
        }
    }

    public ShowHideIgnoredFilesAction() {
        super(getText(),"", Icons.IGNORE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IgnoreSettings.getInstance().toggleIgnoredFilesOnProjectViewVisibility();

        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(getText());
    }

}
