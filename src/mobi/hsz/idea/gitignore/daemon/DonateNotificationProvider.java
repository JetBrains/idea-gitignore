package mobi.hsz.idea.gitignore.daemon;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import mobi.hsz.idea.gitignore.GitignoreBundle;
import mobi.hsz.idea.gitignore.util.Icons;
import mobi.hsz.idea.gitignore.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DonateNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {
    private static final String DONATION_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SJAU4XWQ584QL";
    private static final Key<EditorNotificationPanel> KEY = Key.create(GitignoreBundle.message("daemon.donate.me"));

    private final Project project;
    private final EditorNotifications notifications;

    public DonateNotificationProvider(Project project, @NotNull EditorNotifications notifications) {
        this.project = project;
        this.notifications = notifications;
    }

    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(VirtualFile file, FileEditor fileEditor) {
        if (Properties.isIgnoreDonation(project)) {
            return null;
        }

        return createPanel(project);
    }

    private EditorNotificationPanel createPanel(@NotNull final Project project) {
        final EditorNotificationPanel panel = new EditorNotificationPanel();
        panel.setText(GitignoreBundle.message("daemon.donate"));
        panel.createActionLabel(GitignoreBundle.message("daemon.donate.me"), new Runnable() {
            @Override
            public void run() {
                BrowserUtil.browse(DONATION_URL);
                Properties.setIgnoreDonation(project);
                notifications.updateAllNotifications();
            }
        });
        panel.createActionLabel(GitignoreBundle.message("global.cancel"), new Runnable() {
            @Override
            public void run() {
                Properties.setIgnoreDonation(project);
                notifications.updateAllNotifications();
            }
        });

        try { // ignore if older SDK does not support panel icon
            panel.icon(Icons.FILE);
        } catch (NoSuchMethodError ignored) {}

        return panel;
    }
}
