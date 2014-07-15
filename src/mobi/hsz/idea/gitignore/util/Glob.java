package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Glob {
    public static final String EXCLUDE = "!.git/";

    public static List<VirtualFile> find(VirtualFile root, String glob) {
        return find(root, glob, false);
    }

    public static List<VirtualFile> find(VirtualFile root, String glob, boolean includeNested) {
        List<File> files = new ArrayList<File>();
        String regex = createRegex(glob);
        return walk(root, root, regex, includeNested);
    }

    public static List<String> findAsPaths(VirtualFile root, String glob) {
        return findAsPaths(root, glob, false);
    }

    public static List<String> findAsPaths(VirtualFile root, String glob, boolean includeNested) {
        List<String> list = new ArrayList<String>();
        List<VirtualFile> files = find(root, glob, includeNested);
        for (VirtualFile file : files) {
            list.add(Utils.getRelativePath(root, file));
        }
        return list;
    }

    private static List<VirtualFile> walk(VirtualFile root, VirtualFile directory, String regex, boolean includeNested) {
        List<VirtualFile> files = new ArrayList<VirtualFile>();

        for (VirtualFile file : directory.getChildren()) {
            boolean matches = false;
            String path = Utils.getRelativePath(root, file);
            if (path.equals("/.git")) {
                continue;
            }
            if (regex == null || path.matches(regex)) {
                matches = true;
                files.add(file);
            }
            if (file.isDirectory()) {
                if (includeNested && matches) {
                    regex = null;
                }
                files.addAll(walk(root, file, regex, includeNested));
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
