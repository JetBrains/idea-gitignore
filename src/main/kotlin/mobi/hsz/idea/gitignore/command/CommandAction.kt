// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.command

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable

abstract class CommandAction<T>(private val project: Project) {

    @Throws(Throwable::class)
    protected abstract fun compute(): T

    @Throws(Throwable::class)
    fun execute(): T = WriteCommandAction.writeCommandAction(project).compute(ThrowableComputable(this::compute))
}
