// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Listenable
import org.jdom.Element
import java.util.TreeMap

/**
 * Persistent global settings object for the Ignore plugin.
 */
@State(name = "IgnoreSettings", storages = [Storage("ignore.xml")])
class IgnoreSettings : PersistentStateComponent<Element?>, Listenable<IgnoreSettings.Listener> {

    enum class KEY(private val key: String) {
        ROOT("IgnoreSettings"), MISSING_GITIGNORE("missingGitignore"), USER_TEMPLATES("userTemplates"),
        USER_TEMPLATES_TEMPLATE("template"), USER_TEMPLATES_NAME("name"), LANGUAGES("languages"),
        LANGUAGES_LANGUAGE("language"), LANGUAGES_ID("id"), IGNORED_FILE_STATUS("ignoredFileStatus"),
        INSERT_AT_CURSOR("insertAtCursor"), STARRED_TEMPLATES("starredTemplates"), UNIGNORE_ACTIONS("unignoreActions"),
        HIDE_IGNORED_FILES("hideIgnoredFiles"), NOTIFY_IGNORED_EDITING("notifyIgnoredEditing");

        override fun toString() = key
    }

    /** Notify about missing Gitignore file in the project. */
    var missingGitignore = true
        set(value) {
            notifyOnChange(KEY.MISSING_GITIGNORE, missingGitignore, value)
            field = value
        }

    /** Enable ignored file status coloring. */
    var ignoredFileStatus = true
        set(value) {
            notifyOnChange(KEY.IGNORED_FILE_STATUS, ignoredFileStatus, value)
            field = value
        }

    /** Insert new entries at the cursor's position or at the document end. */
    var insertAtCursor = false
        set(value) {
            notifyOnChange(KEY.INSERT_AT_CURSOR, insertAtCursor, value)
            field = value
        }

    /** Enable unignore actions in context menus. */
    var unignoreActions = true
        set(value) {
            notifyOnChange(KEY.UNIGNORE_ACTIONS, unignoreActions, value)
            field = value
        }

    /** Hide ignored files or folder in the project tree view. */
    var hideIgnoredFiles = false
        set(value) {
            notifyOnChange(KEY.HIDE_IGNORED_FILES, hideIgnoredFiles, value)
            field = value
        }

    /** Shows notification about editing ignored file. */
    var notifyIgnoredEditing = true
        set(value) {
            notifyOnChange(KEY.NOTIFY_IGNORED_EDITING, notifyIgnoredEditing, value)
            field = value
        }

    /** Starred templates. */
    var starredTemplates = listOf<String>()
        set(value) {
            notifyOnChange(KEY.STARRED_TEMPLATES, starredTemplates, value)
            field = value
        }

    /** Settings related to the [IgnoreLanguage]. */
    var languagesSettings = object : IgnoreLanguagesSettings() {
        init {
            IgnoreBundle.LANGUAGES.forEach {
                put(
                    it,
                    object : TreeMap<KEY, Any>() {
                        init {
                            put(KEY.NEW_FILE, true)
                            put(KEY.ENABLE, it.isVCS && !IgnoreBundle.isExcludedFromHighlighting((it)))
                        }
                    }
                )
            }
        }
    }
        set(value) {
            notifyOnChange(KEY.LANGUAGES, languagesSettings, value)
            languagesSettings.clear()
            languagesSettings.putAll(value)
        }

    /** Lists all user defined templates.  */
    var userTemplates = mutableListOf(
        UserTemplate(
            IgnoreBundle.message("settings.userTemplates.default.name"),
            IgnoreBundle.message("settings.userTemplates.default.content")
        )
    )
        set(value) {
            notifyOnChange(KEY.USER_TEMPLATES, userTemplates, value)
            userTemplates.clear()
            userTemplates.addAll(value)
        }

    private val listeners = ContainerUtil.createConcurrentList<Listener>()

    companion object {

        /**
         * Creates [Element] with a list of the [UserTemplate] items.
         *
         * @param userTemplates templates
         * @return [Element] instance with user templates
         */
        fun createTemplatesElement(userTemplates: List<UserTemplate>) =
            Element(KEY.USER_TEMPLATES.toString()).apply {
                userTemplates.forEach {
                    addContent(
                        Element(KEY.USER_TEMPLATES_TEMPLATE.toString()).apply {
                            setAttribute(KEY.USER_TEMPLATES_NAME.toString(), it.name)
                            addContent(it.content)
                        }
                    )
                }
            }

        /**
         * Loads [UserTemplate] objects from the [Element].
         *
         * @param element source
         * @return [UserTemplate] list
         */
        fun loadTemplates(element: Element) = KEY.USER_TEMPLATES.toString().let { key ->
            (element.takeIf { key == element.name } ?: element.getChild(key)).children.map {
                UserTemplate(
                    it.getAttributeValue(KEY.USER_TEMPLATES_NAME.toString()),
                    it.text
                )
            }
        }
    }

    override fun getState() = Element(KEY.ROOT.toString()).apply {
        setAttribute(KEY.MISSING_GITIGNORE.toString(), missingGitignore.toString())
        setAttribute(KEY.IGNORED_FILE_STATUS.toString(), ignoredFileStatus.toString())
        setAttribute(KEY.STARRED_TEMPLATES.toString(), StringUtil.join(starredTemplates, Constants.DOLLAR))
        setAttribute(KEY.UNIGNORE_ACTIONS.toString(), unignoreActions.toString())
        setAttribute(KEY.HIDE_IGNORED_FILES.toString(), hideIgnoredFiles.toString())
        setAttribute(KEY.NOTIFY_IGNORED_EDITING.toString(), notifyIgnoredEditing.toString())

        addContent(
            Element(KEY.LANGUAGES.toString()).apply {
                languagesSettings.forEach { (key, value) ->
                    if (key == null) {
                        return@forEach
                    }
                    addContent(
                        Element(KEY.LANGUAGES_LANGUAGE.toString()).apply {
                            setAttribute(KEY.LANGUAGES_ID.toString(), key.id)
                            value.forEach {
                                setAttribute(it.key.name, it.value.toString())
                            }
                        }
                    )
                }
            }
        )
        addContent(createTemplatesElement(userTemplates))
    }

    override fun loadState(element: Element) {
        element.apply {
            getAttributeValue(KEY.MISSING_GITIGNORE.toString())?.let {
                missingGitignore = it.toBoolean()
            }
            getAttributeValue(KEY.IGNORED_FILE_STATUS.toString())?.let {
                ignoredFileStatus = it.toBoolean()
            }
            getAttributeValue(KEY.STARRED_TEMPLATES.toString())?.let {
                starredTemplates = (StringUtil.split(it, Constants.DOLLAR))
            }
            getAttributeValue(KEY.HIDE_IGNORED_FILES.toString())?.let {
                hideIgnoredFiles = it.toBoolean()
            }
            getAttributeValue(KEY.NOTIFY_IGNORED_EDITING.toString())?.let {
                notifyIgnoredEditing = it.toBoolean()
            }

            getChild(KEY.LANGUAGES.toString()).children.forEach {
                val data = TreeMap<IgnoreLanguagesSettings.KEY, Any>()
                for (key in IgnoreLanguagesSettings.KEY.values()) {
                    data[key] = it.getAttributeValue(key.name)
                }

                val id = it.getAttributeValue(KEY.LANGUAGES_ID.toString())
                val language = IgnoreBundle.LANGUAGES[id]
                languagesSettings[language] = data
            }

            getAttributeValue(KEY.UNIGNORE_ACTIONS.toString())?.let {
                unignoreActions = it.toBoolean()
            }

            userTemplates.clear()
            userTemplates.addAll(loadTemplates(this))
            IgnoreBundle.LANGUAGES
                .filter { !it.isVCS || IgnoreBundle.isExcludedFromHighlighting(it) }
                .forEach { languagesSettings[it]?.apply { this[IgnoreLanguagesSettings.KEY.ENABLE] = false } }
        }
    }

    override fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyOnChange(key: KEY, oldValue: Any, newValue: Any) {
        if (newValue != oldValue) {
            listeners.forEach { it.onChange(key, newValue) }
        }
    }

    fun interface Listener {

        fun onChange(key: KEY, value: Any?)
    }

    /** User defined template model. */
    data class UserTemplate(var name: String = "", var content: String = "") {

        val isEmpty
            get() = name.isEmpty() && content.isEmpty()

        override fun equals(other: Any?) = when {
            other !is UserTemplate -> false
            other === this -> true
            else -> ((name == other.name) && (content == other.content))
        }

        override fun toString() = name

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + content.hashCode()
            return result
        }
    }

    /** Helper class for the [IgnoreLanguage] settings. */
    open class IgnoreLanguagesSettings : LinkedHashMap<IgnoreLanguage?, TreeMap<IgnoreLanguagesSettings.KEY, Any>>() {

        enum class KEY {
            NEW_FILE, ENABLE
        }

        override fun clone(): IgnoreLanguagesSettings {
            val copy = super.clone() as IgnoreLanguagesSettings
            for ((key, value) in copy) {
                copy[key] = value.clone() as TreeMap<KEY, Any>
            }
            return copy
        }
    }
}
