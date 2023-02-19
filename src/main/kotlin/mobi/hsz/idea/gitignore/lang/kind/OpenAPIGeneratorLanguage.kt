// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.OpenAPIGeneratorFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * OpenAPIGenerator [IgnoreLanguage] definition.
 */
class OpenAPIGeneratorLanguage private constructor() : IgnoreLanguage("OpenAPI Generator", "openapi-generator-ignore", null, Icons.OPENAPI_GENERATOR) {

    companion object {
        val INSTANCE = OpenAPIGeneratorLanguage()
    }

    override val fileType
        get() = OpenAPIGeneratorFileType.INSTANCE
}
