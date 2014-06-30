package mobi.hsz.idea.gitignore.util;

public class Utils {

    public static final double JAVA_VERSION = getJavaVersion();

    private static double getJavaVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for ( ; pos<version.length() && count < 2; pos++) {
            if (version.charAt(pos) == '.') count++;
        }
        return Double.parseDouble(version.substring(0, pos - 1));
    }

}
