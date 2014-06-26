package mobi.hsz.idea.gitignore.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

public class PathLookupElement extends LookupElement {
    private final String path;
    private final boolean directory;

    public PathLookupElement(String path, boolean directory) {
        this.path = path;
        this.directory = directory;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setIcon(directory ? PlatformIcons.DIRECTORY_CLOSED_ICON : PlatformIcons.FILE_ICON);
        super.renderElement(presentation);
    }

    @NotNull
    @Override
    public String getLookupString() {
        return path;
    }
}
