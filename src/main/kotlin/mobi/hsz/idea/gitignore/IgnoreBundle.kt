// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore

import com.intellij.AbstractBundle
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.lang.kind.*
import mobi.hsz.idea.gitignore.util.CachedConcurrentMap
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

/**
 * [ResourceBundle]/localization utils for the .ignore support plugin.
 */
object IgnoreBundle : AbstractBundle("messages.IgnoreBundle") {

    @NonNls
    const val BUNDLE_NAME = "messages.IgnoreBundle"

    private val BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)

    val LANGUAGES = IgnoreLanguages(
        listOf(
            BazaarLanguage.INSTANCE,
            ChefLanguage.INSTANCE,
            CloudFoundryLanguage.INSTANCE,
            CvsLanguage.INSTANCE,
            DarcsLanguage.INSTANCE,
            DeployHQLanguage.INSTANCE,
            DockerLanguage.INSTANCE,
            ElasticBeanstalkLanguage.INSTANCE,
            EleventyLanguage.INSTANCE,
            ESLintLanguage.INSTANCE,
            FloobitsLanguage.INSTANCE,
            FossilLanguage.INSTANCE,
            GitLanguage.INSTANCE,
            GoogleCloudLanguage.INSTANCE,
            HelmLanguage.INSTANCE,
            JetpackLanguage.INSTANCE,
            JSHintLanguage.INSTANCE,
            MercurialLanguage.INSTANCE,
            MonotoneLanguage.INSTANCE,
            NodemonLanguage.INSTANCE,
            NpmLanguage.INSTANCE,
            NuxtJSLanguage.INSTANCE,
            OpenAPIGeneratorLanguage.INSTANCE,
            PerforceLanguage.INSTANCE,
            PrettierLanguage.INSTANCE,
            SourcegraphLanguage.INSTANCE,
            StyleLintLanguage.INSTANCE,
            StylintLanguage.INSTANCE,
            SwaggerCodegenLanguage.INSTANCE,
            TerraformLanguage.INSTANCE,
            TFLanguage.INSTANCE,
            TokeiLanguage.INSTANCE,
            UpLanguage.INSTANCE,
            VercelLanguage.INSTANCE,
            YarnLanguage.INSTANCE,
        )
    )

    /**Highlighting for the mentioned languages already provided by IDEA core */
    private val IGNORE_LANGUAGES_HIGHLIGHTING_EXCLUDED = arrayOf(
        GitLanguage.INSTANCE,
        MercurialLanguage.INSTANCE,
        PerforceLanguage.INSTANCE
    )

    /** Available IgnoreFileType instances filtered with [IgnoreLanguage.isVCS] condition. */
    val VCS_LANGUAGES = IgnoreLanguages(
        LANGUAGES.filter(IgnoreLanguage::isVCS)
    )

    /** Contains information about enabled/disabled languages. */
    val ENABLED_LANGUAGES = CachedConcurrentMap.create<IgnoreFileType, Boolean> { key -> key.ignoreLanguage.isEnabled }

    /**
     * Loads a [String] from the [.BUNDLE] [ResourceBundle].
     *
     * @param key    the key of the resource
     * @param params the optional parameters for the specific resource
     * @return the [String] value or `null` if no resource found for the key
     */
    fun message(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any?) = message(BUNDLE, key, *params)

    /**
     * Loads a [String] from the [.BUNDLE] [ResourceBundle].
     *
     * @param key    the key of the resource
     * @param params the optional parameters for the specific resource
     * @return the [String] value or `null` if no resource found for the key
     */
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any?) = getLazyMessage(key, *params)

    /**
     * Returns [IgnoreLanguage] matching to the given [VirtualFile].
     *
     * @param file to obtain
     * @return matching language
     */
    fun obtainLanguage(file: VirtualFile) = LANGUAGES.find { it.filename == file.name }

    fun isExcludedFromHighlighting(language: IgnoreLanguage) = IGNORE_LANGUAGES_HIGHLIGHTING_EXCLUDED.contains(language)

    /** Available syntax list. */
    enum class Syntax {
        GLOB, REGEXP;

        override fun toString() = super.toString().lowercase()

        /**
         * Returns [mobi.hsz.idea.gitignore.psi.IgnoreTypes.SYNTAX] element presentation.
         *
         * @return element presentation
         */
        val presentation
            get() = StringUtil.join(KEY, " ", toString())

        companion object {
            @NonNls
            private val KEY = "syntax:"

            fun find(name: String?) = when (name) {
                null -> null
                else ->
                    try {
                        valueOf(name.uppercase())
                    } catch (iae: IllegalArgumentException) {
                        null
                    }
            }
        }
    }

    /**
     * Simple [ArrayList] with method to find [IgnoreLanguage] by its name.
     */
    class IgnoreLanguages(languages: List<IgnoreLanguage>) : ArrayList<IgnoreLanguage>(languages) {

        operator fun get(id: String) = find { id == it.id }
    }
}
