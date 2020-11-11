// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template

import java.util.Comparator

/**
 * [TemplateTreeComparator] class implements [Comparator] for the template nodes.
 */
class TemplateTreeComparator : Comparator<TemplateTreeNode> {

    /**
     * Compares its two arguments for order. If any of given object isn't a template, returns `0`.
     *
     * @param o1 first object
     * @param o2 second object
     * @return comparison result
     */
    override fun compare(o1: TemplateTreeNode, o2: TemplateTreeNode) =
        when {
            o2.template == null || o1.template == null -> 0
            else -> o1.toString().compareTo(o2.toString())
        }
}
