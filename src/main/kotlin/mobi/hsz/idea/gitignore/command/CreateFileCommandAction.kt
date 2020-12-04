// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.command

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.file.IgnoreTemplatesFactory
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

/**
 * Command action that creates new file in given directory.
 */
class CreateFileCommandAction(project: Project, val directory: PsiDirectory, val fileType: IgnoreFileType) :
    CommandAction<PsiFile>(project) {

    /**
     * Creates a new file using [IgnoreTemplatesFactory.createFromTemplate] to fill it with content.
     *
     * @return created file
     */
    override fun compute() = IgnoreTemplatesFactory(fileType).createFromTemplate(directory)
}
