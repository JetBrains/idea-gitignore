package mobi.hsz.idea.gitignore.treeviewfilter.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreManager;
import mobi.hsz.idea.gitignore.util.CommonDataKeys;

/**
 * Created by maxi on 03/01/17.
 */
public class ShowHideIgnoredFilesAction extends AnAction {


    public ShowHideIgnoredFilesAction() {
        super(IgnoreBundle.message("action.toggleIgnoredVisibility"), IgnoreBundle.message("action.toggleIgnoredVisibility"), null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IgnoreManager ignoreManager = IgnoreManager.getInstance(e.getProject());

        final VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        for (VirtualFile f : files) {
            System.out.println(f.getName());
            System.out.println("ignored: " + ignoreManager.isFileIgnored(f));
        }
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        final Project project = e.getProject();

        if (project == null || files == null || (files.length == 1 && files[0].equals(project.getBaseDir()))) {
            e.getPresentation().setVisible(false);
        }
    }
}
