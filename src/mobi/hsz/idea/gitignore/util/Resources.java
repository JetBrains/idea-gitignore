package mobi.hsz.idea.gitignore.util;

import mobi.hsz.idea.gitignore.GitignoreLanguage;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Resources {

    private static File[] files;
    private static File directory;

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();

        for (File file : getGitignoreFiles()) {
            templates.add(file.getName());
        }

//        InputStream foo = Resources.class.getResourceAsStream("/gitignore/Ada.gitignore");

        return templates;
    }


    /**
     * Returns list of gitignore templates
     *
     * @return Template files
     */
    protected static File[] getGitignoreFiles() {
        if (files == null) {
            files = getResource("/gitignore").listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(GitignoreLanguage.FILENAME);
                }
            });
        }
        return files;
    }

    /**
     * Returns gitignore templates directory
     *
     * @return Resources directory
     */
    public static File getResource(String path) {
        if (directory == null) {
            URL resource = Resources.class.getResource(path);
            assert resource != null;
            directory = new File(resource.getPath());
        }
        return directory;
    }

    /**
     * Reads resource file and returns its content as a String
     *
     * @param path Resource path
     * @return Content
     */
    public static String getResourceContent(String path) {
        return convertStreamToString(Resources.class.getResourceAsStream(path));
    }

    /**
     * Converts InputStream resource to String
     *
     * @param inputStream Input stream
     * @return Content
     */
    protected static String convertStreamToString(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
