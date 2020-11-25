// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.vfs.VirtualFile

/**
 * Action that adds currently selected [VirtualFile] to the specified Ignore [VirtualFile] as unignored.
 * Action is added to the IDE context menus not directly but with [UnignoreFileGroupAction] action.
 */
class UnignoreFileAction(virtualFile: VirtualFile?) :
    IgnoreFileAction(virtualFile, getFileType(virtualFile), "action.addToUnignore", "action.addToUnignore.description") {

    /**
     * Gets the file's path relative to the specified root directory.
     * Returns string with negation.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    override fun getPath(root: VirtualFile, file: VirtualFile) = super.getPath(root, file).run {
        when {
            isEmpty() -> this
            else -> "!$this"
        }
    }
}
