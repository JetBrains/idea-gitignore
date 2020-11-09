// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.psi

import com.intellij.psi.tree.IElementType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import org.jetbrains.annotations.NonNls

/**
 * Custom element type.
 */
class IgnoreElementType(@NonNls debugName: String) : IElementType(debugName, IgnoreLanguage.INSTANCE)
