/*
 * The MIT License (MIT)
 *
 * Copyright (c) today.year hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.util;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Util class that holds common {@link DataKey} list.
 * 
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.6
 */
public class CommonDataKeys {
    /** {@link Project} data key */
    public static final DataKey<Project> PROJECT = DataKey.create("project");

    /** {@link Editor} data key */
    public static final DataKey<Editor> EDITOR = DataKey.create("editor");

    /** {@link Editor} data key */
    public static final DataKey<Editor> HOST_EDITOR = DataKey.create("host.editor");

    /** {@link Caret} data key */
    public static final DataKey<Caret> CARET = DataKey.create("caret");

    /** {@link Editor} data key */
    public static final DataKey<Editor> EDITOR_EVEN_IF_INACTIVE = DataKey.create("editor.even.if.inactive");

    /** {@link Navigatable} data key */
    public static final DataKey<Navigatable> NAVIGATABLE = DataKey.create("Navigatable");

    /** {@link Navigatable} array data key */
    public static final DataKey<Navigatable[]> NAVIGATABLE_ARRAY = DataKey.create("NavigatableArray");

    /** {@link VirtualFile} data key */
    public static final DataKey<VirtualFile> VIRTUAL_FILE = DataKey.create("virtualFile");

    /** {@link VirtualFile} array data key */
    public static final DataKey<VirtualFile[]> VIRTUAL_FILE_ARRAY = DataKey.create("virtualFileArray");

    /** {@link PsiElement} data key */
    public static final DataKey<PsiElement> PSI_ELEMENT = DataKey.create("psi.Element");

    /** {@link PsiFile} data key */
    public static final DataKey<PsiFile> PSI_FILE = DataKey.create("psi.File");

    /** Private constructor to prevent creating {@link CommonDataKeys} instance. */
    private CommonDataKeys() {
    }
}
