package mobi.hsz.idea.gitignore.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import mobi.hsz.idea.gitignore.GitignoreBundle;

public class Properties {
    private static final String PROP_IGNORE_MISSING_GITIGNORE = "ignore_missing_gitignore";
    private static final String PROP_IGNORE_DONATION = "ignore_donation_" + GitignoreBundle.VERSION;

    private Properties() {
    }

    public static boolean isIgnoreMissingGitignore(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(PROP_IGNORE_MISSING_GITIGNORE, false);
    }

    public static boolean isIgnoreDonation(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(PROP_IGNORE_DONATION, false);
    }

    public static void setIgnoreMissingGitignore(Project project) {
        PropertiesComponent.getInstance(project).setValue(PROP_IGNORE_MISSING_GITIGNORE, Boolean.TRUE.toString());
    }

    public static void setIgnoreDonation(Project project) {
        PropertiesComponent.getInstance(project).setValue(PROP_IGNORE_DONATION, Boolean.TRUE.toString());
    }
}
