package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

public class PathLookupElement extends LookupElement {
    private final String path;
    private final FileType type;
    private final boolean directory;

    public PathLookupElement(String path, FileType type, boolean directory) {
        this.path = path;
        this.type = type;
        this.directory = directory;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setIcon(directory ? PlatformIcons.FOLDER_ICON : type.getIcon());
        super.renderElement(presentation);
    }

    @NotNull
    @Override
    public String getLookupString() {
        return path + (directory ? "/" : "");
    }
}
