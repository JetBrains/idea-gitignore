// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.ui.template

import com.intellij.ui.CheckedTreeNode
import mobi.hsz.idea.gitignore.util.Resources

/**
 * [TemplateTreeNode] is an implementation of checkbox tree node.
 */
class TemplateTreeNode : CheckedTreeNode {

    val template: Resources.Template?
    val container: Resources.Template.Container?

    constructor() : super(null) {
        template = null
        container = null
    }

    constructor(container: Resources.Template.Container?) : super(container) {
        template = null
        this.container = container
    }

    constructor(template: Resources.Template) : super(template) {
        this.template = template
        container = template.container
    }
}
