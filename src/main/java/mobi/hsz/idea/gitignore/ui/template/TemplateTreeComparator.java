// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template;

import java.util.Comparator;

/**
 * {@link TemplateTreeComparator} class implements {@link Comparator} for the template nodes.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6
 */
public class TemplateTreeComparator implements Comparator<TemplateTreeNode> {
    /**
     * Compares its two arguments for order. If any of given object isn't a template, returns <code>0</code>.
     *
     * @param o1 first object
     * @param o2 second object
     * @return comparison result
     */
    @Override
    public int compare(TemplateTreeNode o1, TemplateTreeNode o2) {
        if (o2.getTemplate() == null || o1.getTemplate() == null) {
            return 0;
        }
        return o1.toString().compareTo(o2.toString());
    }
}
