package mobi.hsz.idea.gitignore.util;

import mobi.hsz.idea.gitignore.GitignoreLanguage;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Resources {

    private static File[] files;
    private static File directory;

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();

        for (File template : getFiles()) {
            templates.add(template.getName());
        }

//        InputStream foo = Resources.class.getResourceAsStream("/gitignore/Ada.gitignore");

        return templates;
    }


    /**
     * Returns list of gitignore templates
     *
     * @return Template files
     */
    protected static File[] getFiles() {
        if (files == null) {
            files = getDirectory().listFiles(new FileFilter() {
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
    public static File getDirectory() {
        if (directory == null) {
            directory = new File(Resources.class.getResource("/gitignore/").getPath());
        }
        return directory;
    }

    /**
     * Reads resource file and returns its content as a String
     *
     * @param path Resource path
     * @return Content
     */
    public static String getTemplate(String path) {
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
