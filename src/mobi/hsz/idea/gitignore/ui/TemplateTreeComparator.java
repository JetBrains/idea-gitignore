package mobi.hsz.idea.gitignore.ui;

import java.util.Comparator;

public class TemplateTreeComparator implements Comparator<TemplateTreeNode> {
    @Override
    public int compare(TemplateTreeNode o1, TemplateTreeNode o2) {
        if (o2.getTemplate() == null || o1.getTemplate() == null) {
            return 0;
        }
        return o1.toString().compareTo(o2.toString());
    }
}
