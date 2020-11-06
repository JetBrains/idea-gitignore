// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Util class that holds common [DataKey] list.
 */
object CommonDataKeys {
    /** [Project] data key  */
    val PROJECT = DataKey.create<Project>("project")

    /** [Editor] data key  */
    val EDITOR = DataKey.create<Editor>("editor")

    /** [Editor] data key  */
    val HOST_EDITOR = DataKey.create<Editor>("host.editor")

    /** [Caret] data key  */
    val CARET = DataKey.create<Caret>("caret")

    /** [Editor] data key  */
    val EDITOR_EVEN_IF_INACTIVE = DataKey.create<Editor>("editor.even.if.inactive")

    /** [Navigatable] data key  */
    val NAVIGATABLE = DataKey.create<Navigatable>("Navigatable")

    /** [Navigatable] array data key  */
    val NAVIGATABLE_ARRAY = DataKey.create<Array<Navigatable>>("NavigatableArray")

    /** [VirtualFile] data key  */
    val VIRTUAL_FILE = DataKey.create<VirtualFile>("virtualFile")

    /** [VirtualFile] array data key  */
    val VIRTUAL_FILE_ARRAY = DataKey.create<Array<VirtualFile>>("virtualFileArray")

    /** [PsiElement] data key  */
    val PSI_ELEMENT = DataKey.create<PsiElement>("psi.Element")

    /** [PsiFile] data key  */
    val PSI_FILE = DataKey.create<PsiFile>("psi.File")
}
