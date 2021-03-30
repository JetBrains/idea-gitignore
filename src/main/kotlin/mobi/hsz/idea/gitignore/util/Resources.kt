// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.openapi.components.service
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.settings.IgnoreSettings.UserTemplate
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.Scanner

/**
 * [Resources] util class that contains methods that work on plugin resources.
 */
object Resources {

    /** Path to the gitignore templates list.  */
    @NonNls
    private val GITIGNORE_TEMPLATES_PATH = "/templates.list"

    /**
     * Returns list of gitignore templates.
     *
     * @return Gitignore templates list
     */
    val gitignoreTemplates: List<Template>
        get() {
            val resourceTemplates = mutableListOf<Template>()
            val settings = service<IgnoreSettings>()
            val starredTemplates = settings.starredTemplates

            // fetch templates from resources
            try {
                getResourceContent(GITIGNORE_TEMPLATES_PATH)?.run {
                    lines().map {
                        val line = "/$it"
                        getResource(line)?.let { file ->
                            val content = getResourceContent(line)
                            val template = Template(file, content)
                            template.isStarred = starredTemplates.contains(template.name)
                            resourceTemplates.add(template)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return resourceTemplates + settings.userTemplates.map { Template(it) }
        }

    /**
     * Returns gitignore templates directory.
     *
     * @return Resources directory
     */
    private fun getResource(path: String) = Resources::class.java.getResource(path)?.run { File(path) }

    /**
     * Reads resource file and returns its content as a String.
     *
     * @param path Resource path
     * @return Content
     */
    fun getResourceContent(path: String) = convertStreamToString(Resources::class.java.getResourceAsStream(path))

    /**
     * Converts InputStream resource to String.
     *
     * @param inputStream Input stream
     * @return Content
     */
    private fun convertStreamToString(inputStream: InputStream?) =
        inputStream?.let { stream -> Scanner(stream).useDelimiter("\\A").takeIf { it.hasNext() }?.next() ?: "" }

    /** [Template] entity that defines template fetched from resources or [IgnoreSettings].  */
    class Template : Comparable<Template> {

        /** [File] pointer. `null` if template is fetched from [IgnoreSettings].  */
        val file: File?

        /** Template name.  */
        val name: String

        /** Template content.  */
        val content: String?

        /** Template's [Container].  */
        val container: Container
            get() = if (isStarred) Container.STARRED else field

        /** Template is starred.  */
        var isStarred = false

        /**
         * Defines if template is fetched from resources ([Container.ROOT] directory or [Container.GLOBAL]
         * subdirectory) or is user defined and fetched from [IgnoreSettings].
         */
        enum class Container {
            USER, STARRED, ROOT, GLOBAL
        }

        constructor(file: File, content: String?) {
            this.file = file
            name = file.name.replace(GitLanguage.INSTANCE.filename, "")
            this.content = content
            container = if (file.parent.endsWith("Global")) Container.GLOBAL else Container.ROOT
        }

        constructor(userTemplate: UserTemplate) {
            file = null
            name = userTemplate.name
            content = userTemplate.content
            container = Container.USER
        }

        override fun toString() = name

        override fun compareTo(other: Template) = name.compareTo(other.name, ignoreCase = true)
    }
}
