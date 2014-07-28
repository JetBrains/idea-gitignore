package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreFileGroupAction extends ActionGroup {
    private final List<VirtualFile> files = new ArrayList<VirtualFile>();
    private VirtualFile baseDir;

    public IgnoreFileGroupAction() {
        Presentation p = getTemplatePresentation();
        p.setText(GitignoreBundle.message("action.addToGitignore"));
        p.setDescription(GitignoreBundle.message("action.addToGitignore.description"));
        p.setIcon(Icons.FILE);
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        final Project project = e.getData(CommonDataKeys.PROJECT);
        files.clear();
        if (project != null && file != null) {
            files.addAll(Utils.getSuitableGitignoreFiles(project, file));
            Collections.reverse(files);
            baseDir = project.getBaseDir();
        }
        setPopup(files.size() > 1);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        AnAction[] actions;
        int size = files.size();
        if (size == 0) {
            actions = new AnAction[]{ new IgnoreFileAction() };
        } else {
            actions = new AnAction[size];
            for (int i = 0; i < files.size(); i++) {
                VirtualFile file = files.get(i);
                IgnoreFileAction action = new IgnoreFileAction(file);
                actions[i] = action;

                if (size > 1) {
                    String name = Utils.getRelativePath(baseDir, file);
                    action.getTemplatePresentation().setText(name);
                }
            }
        }
        return actions;
    }
}
