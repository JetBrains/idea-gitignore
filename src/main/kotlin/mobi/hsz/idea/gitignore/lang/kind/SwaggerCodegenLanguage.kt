// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.SwaggerCodegenFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Swagger Codegen [SwaggerCodegenLanguage] definition
 */
class SwaggerCodegenLanguage private constructor() :
    IgnoreLanguage("Swagger Codegen", "swagger-codegen-ignore", null, Icons.SWAGGER_CODEGEN) {

    companion object {
        val INSTANCE = SwaggerCodegenLanguage()
    }

    override val fileType
        get() = SwaggerCodegenFileType.INSTANCE
}
