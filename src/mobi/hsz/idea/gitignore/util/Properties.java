package mobi.hsz.idea.gitignore.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

public class Properties {
    private static final String PROP_IGNORE_MISSING_GITIGNORE = "ignore_missing_gitignore";

    public static boolean getIgnoreMissingGitignore(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(PROP_IGNORE_MISSING_GITIGNORE, false);
    }

    public static void setIgnoreMissingGitignore(Project project, boolean value) {
        PropertiesComponent.getInstance(project).setValue(PROP_IGNORE_MISSING_GITIGNORE, String.valueOf(value));
    }
}
