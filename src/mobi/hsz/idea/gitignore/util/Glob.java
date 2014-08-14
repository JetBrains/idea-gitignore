package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Glob {
    private static final HashMap<String, String> cache = new HashMap<String, String>();

    private Glob() {
    }

    public static List<VirtualFile> find(VirtualFile root, String glob) {
        return find(root, glob, false);
    }

    public static List<VirtualFile> find(final VirtualFile root, String glob, final boolean includeNested) {
        Pattern pattern = createPattern(glob);
        if (pattern == null) {
            return Collections.emptyList();
        }

        final List<VirtualFile> files = ContainerUtil.newArrayList();

        VirtualFileVisitor<Pattern> visitor = new VirtualFileVisitor<Pattern>(VirtualFileVisitor.NO_FOLLOW_SYMLINKS) {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                boolean matches = false;
                Pattern pattern = getCurrentValue();
                String path = Utils.getRelativePath(root, file);

                if (path == null || Utils.isGitDirectory(path)) {
                    return false;
                }

                if (pattern == null || pattern.matcher(path).matches()) {
                    matches = true;
                    files.add(file);
                }

                setValueForChildren(includeNested && matches ? null : pattern);
                return true;
            }
        };
        visitor.setValueForChildren(pattern);
        VfsUtil.visitChildrenRecursively(root, visitor);

        return files;
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

    private static List<VirtualFile> walk(VirtualFile root, VirtualFile directory, @Nullable Pattern pattern, boolean includeNested) {
        List<VirtualFile> files = ContainerUtil.newArrayList();
        for (VirtualFile file : directory.getChildren()) {
            boolean matches = false;
            String path = Utils.getRelativePath(root, file);
            if (path == null) {
                continue;
            }

            if ("/.git".equals(path)) {
                continue;
            }

            if (pattern == null || pattern.matcher(path).matches()) {
                matches = true;
                files.add(file);
            }

            if (file.isDirectory()) {
                files.addAll(walk(root, file, includeNested && matches ? null : pattern, includeNested));
            }
        }

        return files;
    }
    
    public static Pattern createPattern(@NotNull String glob) {
        try {
            return Pattern.compile(createRegex(glob));
        } catch (PatternSyntaxException e) {
            return null;
        }
    }

    public static String createRegex(String glob) {
        String cached = cache.get(glob);
        if (cached != null) {
            return cached;
        }

        char[] chars;
        StringBuilder sb = new StringBuilder();

        sb.append("^");
        if (!glob.startsWith("/")) {
            if (!glob.startsWith("**")) {
                if (glob.indexOf('/') == -1) {
                    sb.append("([^/]*?/)*");
                }
            }
            chars = glob.toCharArray();
        } else {
            chars = glob.substring(1).toCharArray();
        }

        boolean escape = false, star = false, doubleStar = false, bracket = false;
        for (char ch : chars) {
            if (bracket && ch != ']') {
                sb.append(ch);
                continue;
            } else if (doubleStar) {
                doubleStar = false;
                if (ch == '/') {
                    sb.append("([^/]*/)*?");
                    continue;
                } else {
                    sb.append("[^/]*?");
                }
            }

            if (ch == '*') {
                if (escape) {
                    sb.append("\\*");
                    escape = false;
                    star = false;
                } else if (star) {
                    char prev = sb.charAt(sb.length() - 1);
                    if (prev == '^' || prev == '/') {
                        doubleStar = true;
                    } else {
                        sb.append("[^/]*?");
                    }
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

        if (star || doubleStar) {
            sb.append("[^/]+");
        } else if (sb.charAt(sb.length() - 1) == '/') {
            sb.append("?");
        }
        sb.append("/?");

        if (escape) {
            // should not be alone
        }
        sb.append('$');

        cache.put(glob, sb.toString());

        return sb.toString();
    }

}
