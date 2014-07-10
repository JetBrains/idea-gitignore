package mobi.hsz.idea.gitignore.util;

import com.esotericsoftware.wildcard.Paths;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

public class Glob {
    private static final String EXCLUDE = "!.git/";

    public static List<File> find(VirtualFile root, String glob) {
        if (!glob.startsWith("/")) {
            glob = "**/" + glob;
        }
        Paths paths = new Paths(root.getPath(), EXCLUDE, glob);
        List<File> files = paths.getFiles();

        ListIterator<File> it = files.listIterator();
        while (it.hasNext()) {
            if (!it.next().exists()) {
                it.remove();
            }
        }

        return files;
    }

}
