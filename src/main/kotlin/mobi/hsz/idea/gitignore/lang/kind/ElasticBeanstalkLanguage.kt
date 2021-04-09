// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.lang.kind

import mobi.hsz.idea.gitignore.file.type.kind.ElasticBeanstalkFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Icons

/**
 * ElasticBeanstalk [IgnoreLanguage] definition.
 */
class ElasticBeanstalkLanguage private constructor() : IgnoreLanguage("ElasticBeanstalk", "ebignore", null, Icons.ELASTIC_BEANSTALK) {

    companion object {
        val INSTANCE = ElasticBeanstalkLanguage()
    }

    override val fileType
        get() = ElasticBeanstalkFileType.INSTANCE

    override val isVCS
        get() = false
}
