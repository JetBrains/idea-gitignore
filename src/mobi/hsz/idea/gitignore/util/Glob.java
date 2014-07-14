package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Glob {
    public static final String EXCLUDE = "!.git/";

    public static List<VirtualFile> find(VirtualFile root, String glob) {
        return find(root, glob, true);
    }

    public static List<VirtualFile> find(VirtualFile root, String glob, boolean ignoreNested) {
        List<File> files = new ArrayList<File>();
        String regex = createRegex(glob);
        return walk(root, root, regex, ignoreNested);
    }

    public static List<String> findAsPaths(VirtualFile root, String glob) {
        return findAsPaths(root, glob, true);
    }

    public static List<String> findAsPaths(VirtualFile root, String glob, boolean ignoreNested) {
        List<String> list = new ArrayList<String>();
        List<VirtualFile> files = find(root, glob, ignoreNested);
        for (VirtualFile file : files) {
            list.add(Utils.getRelativePath(root, file));
        }
        return list;
    }

    private static List<VirtualFile> walk(VirtualFile root, VirtualFile directory, String regex, boolean ignoreNested) {
        List<VirtualFile> files = new ArrayList<VirtualFile>();

        for (VirtualFile file : directory.getChildren()) {
            String path = Utils.getRelativePath(root, file);
            if (path.equals("/.git")) {
                continue;
            }
            if (path.matches(regex)) {
                files.add(file);
            }
            if (!ignoreNested && file.isDirectory()) {
                files.addAll(walk(root, file, regex, false));
            }
        }

        return files;
    }

    public static String createRegex(String glob) {
        StringBuilder sb = new StringBuilder();

        sb.append("^");
        if (!glob.startsWith("/")) {
            if (!glob.startsWith("*")) {
                sb.append(".*");
            }
            sb.append("(/[^/]*)*");
        }

        boolean escape = false, star = false, bracket = false;
        for (char ch : glob.toCharArray()) {
            if (bracket && ch != ']') {
                sb.append(ch);
                continue;
            }

            if (ch == '*') {
                if (escape) {
                    sb.append("\\*");
                    escape = false;
                    star = false;
                } else if (star) {
                    sb.append("(/[^/]*)*?");
                    star = false;
                } else {
                    star = true;
                }
                continue;
            } else if (star) {
                sb.append("[^/]*?");
                star = false;
            }

            switch (ch) {

                case '\\':
                    if (escape) {
                        sb.append("\\\\");
                        escape = false;
                    } else {
                        escape = true;
                    }
                    break;

                case '?':
                    if (escape) {
                        sb.append("\\?");
                        escape = false;
                    } else {
                        sb.append('.');
                    }
                    break;

                case '[':
                    if (escape) {
                        sb.append('\\');
                        escape = false;
                    } else {
                        bracket = true;
                    }
                    sb.append(ch);
                    break;

                case ']':
                    if (!bracket || escape) {
                        sb.append('\\');
                    }
                    sb.append(ch);
                    bracket = false;
                    escape = false;
                    break;

                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    sb.append('\\');
                    sb.append(ch);
                    escape = false;
                    break;

                default:
                    escape = false;
                    sb.append(ch);

            }
        }

        if (star) {
            sb.append(".*");
        }
        if (glob.endsWith("/")) {
            sb.append("?");
        }
        if (escape) {
            // should not be alone
        }
        sb.append('$');

        return sb.toString();
    }

}
