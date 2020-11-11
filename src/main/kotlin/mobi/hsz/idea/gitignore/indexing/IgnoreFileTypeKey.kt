// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.indexing

import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

/**
 * Decorator for [IgnoreFileType] to provide less unique hashcode when used with [IgnoreFilesIndex].
 */
class IgnoreFileTypeKey(val type: IgnoreFileType) {

    override fun equals(other: Any?) = other is IgnoreFileTypeKey && other.type.languageName == type.languageName

    override fun hashCode() = type.languageName.hashCode()
}
