package mobi.hsz.idea.gitignore.hideignoredfiles;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import mobi.hsz.idea.gitignore.util.Icons;


/**
 * Created by maxi on 03/01/17.
 */
public class HideIgnoredFilesAction extends AnAction {

    private static String getText() {
        if (IgnoreSettings.getInstance().shouldHideIgnoredFilesOnProjectView()) {
            return IgnoreBundle.message("action.showIgnoredVisibility");
        }
        else {
            return IgnoreBundle.message("action.hideIgnoredVisibility");
        }
    }

    public HideIgnoredFilesAction() {
        super(getText(),"", Icons.IGNORE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IgnoreSettings.getInstance().toggleIgnoredFilesVisibility();

        Presentation presentation = this.getTemplatePresentation();
        presentation.setText(getText());
    }

}
