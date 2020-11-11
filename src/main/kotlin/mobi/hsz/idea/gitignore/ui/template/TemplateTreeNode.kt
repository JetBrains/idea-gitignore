// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template

import com.intellij.ui.CheckedTreeNode
import mobi.hsz.idea.gitignore.util.Resources

/**
 * [TemplateTreeNode] is an implementation of checkbox tree node.
 */
class TemplateTreeNode : CheckedTreeNode {

    /** Current [Resources.Template] element.  */
    val template: Resources.Template?

    /** Current [Resources.Template.Container].  */
    val container: Resources.Template.Container?

    /** Creates a new instance of [TemplateTreeNode].  */
    constructor() : super(null) {
        template = null
        container = null
    }

    /**
     * Creates a new instance of [TemplateTreeNode].
     *
     * @param container current templates container
     */
    constructor(container: Resources.Template.Container?) : super(container) {
        template = null
        this.container = container
    }

    /**
     * Creates a new instance of [TemplateTreeNode].
     *
     * @param template current template
     */
    constructor(template: Resources.Template) : super(template) {
        this.template = template
        container = template.container
    }
}
