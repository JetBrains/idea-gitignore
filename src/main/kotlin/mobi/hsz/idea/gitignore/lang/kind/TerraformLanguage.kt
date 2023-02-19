// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.TerraformFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * TerraformLanguage [IgnoreLanguage] definition.
 */
class TerraformLanguage private constructor() : IgnoreLanguage("Terraform", "terraformignore", null, Icons.TERRAFORM) {

    companion object {
        val INSTANCE = TerraformLanguage()
    }

    override val fileType
        get() = TerraformFileType.INSTANCE
}
