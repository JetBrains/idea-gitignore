// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util.exec

import com.intellij.execution.process.BaseOSProcessHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.text.nullize
import com.intellij.vcsUtil.VcsUtil
import git4idea.config.GitExecutableManager
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.lang.kind.GitLanguage
import mobi.hsz.idea.gitignore.util.Utils
import mobi.hsz.idea.gitignore.util.exec.parser.ExecutionOutputParser
import mobi.hsz.idea.gitignore.util.exec.parser.GitExcludesOutputParser
import mobi.hsz.idea.gitignore.util.exec.parser.GitUnignoredFilesOutputParser
import mobi.hsz.idea.gitignore.util.exec.parser.SimpleOutputParser
import org.jetbrains.annotations.NonNls
import org.jetbrains.jps.service.SharedThreadPool
import java.io.File
import java.io.IOException
import java.util.concurrent.Future

/**
 * Class that holds util methods for calling external executables (i.e. git/hg)
 */
object ExternalExec {

    /** Default external exec timeout.  */
    private const val DEFAULT_TIMEOUT = 5000

    /** Checks if Git plugin is enabled.  */
    private val GIT_ENABLED = Utils.isGitPluginEnabled

    /** Git command to get user's excludesfile path.  */
    @NonNls
    private val GIT_CONFIG_EXCLUDES_FILE = "config --global core.excludesfile"

    /** Global gitignore file located in user dir.  */
    val GIT_USER_IGNORE = Utils.resolveUserDir("~/.config/git/ignore")?.let { VcsUtil.getVirtualFile(it) }

    /** Git command to list unversioned files.  */
    @NonNls
    private val GIT_UNIGNORED_FILES = "clean -dn"

    /** Git command to list ignored but tracked files.  */
    @NonNls
    private val GIT_IGNORED_FILES = "ls-files -i --exclude-standard"

    /**
     * Returns [VirtualFile] instance of the Git excludes file if available.
     *
     * @return Git excludes file
     */
    val gitExcludesFile: VirtualFile by lazy {
        runForSingle(GitLanguage.INSTANCE, GIT_CONFIG_EXCLUDES_FILE, null, GitExcludesOutputParser())
    }

    /**
     * Returns list of unignored files for the given directory.
     *
     * @param language to check
     * @param project  current project
     * @param file     current file
     * @return unignored files list
     */
    fun getUnignoredFiles(language: IgnoreLanguage, project: Project, file: VirtualFile): List<String> {
        if (!Utils.isInProject(file, project)) {
            return emptyList()
        }
        return run(
            language,
            GIT_UNIGNORED_FILES,
            file.parent,
            GitUnignoredFilesOutputParser()
        ) ?: emptyList()
    }

    /**
     * Returns list of ignored files for the given repository.
     *
     * @param vcsRoot repository to check
     * @return unignored files list
     */
    @JvmStatic
    fun getIgnoredFiles(vcsRoot: VcsRoot) = run(
        GitLanguage.INSTANCE,
        GIT_IGNORED_FILES,
        vcsRoot.path,
        SimpleOutputParser()
    ) ?: emptyList()

    /**
     * Returns path to the [IgnoreLanguage] binary or null if not available.
     * Currently only  [GitLanguage] is supported.
     *
     * @param language current language
     * @return path to binary
     */
    private fun bin(language: IgnoreLanguage) = when (GitLanguage.INSTANCE == language && GIT_ENABLED) {
        true -> GitExecutableManager.getInstance().pathToGit.nullize()
        false -> null
    }

    /**
     * Runs [IgnoreLanguage] executable with the given command and current working directory.
     *
     * @param language  current language
     * @param command   to call
     * @param directory current working directory
     * @param parser    [ExecutionOutputParser] implementation
     * @param <T>       return type
     * @return result of the call
    </T> */
    private fun <T> runForSingle(language: IgnoreLanguage, command: String, directory: VirtualFile?, parser: ExecutionOutputParser<T>) =
        ContainerUtil.getFirstItem(run(language, command, directory, parser))

    /**
     * Runs [IgnoreLanguage] executable with the given command and current working directory.
     *
     * @param language  current language
     * @param command   to call
     * @param directory current working directory
     * @param parser    [ExecutionOutputParser] implementation
     * @param <T>       return type
     * @return result of the call
     */
    private fun <T> run(language: IgnoreLanguage, command: String, directory: VirtualFile?, parser: ExecutionOutputParser<T>?): List<T>? {
        val bin = bin(language) ?: return null

        directory?.run { File(path) }?.let { workingDirectory ->
            try {
                val cmd = "$bin $command"
                val process = Runtime.getRuntime().exec(cmd, null, workingDirectory)

                object : BaseOSProcessHandler(process, StringUtil.join(cmd, " "), null) {
                    override fun executeTask(task: Runnable): Future<*> {
                        return SharedThreadPool.getInstance().submit(task)
                    }

                    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
                        parser?.onTextAvailable(text, outputType)
                    }
                }.run {
                    startNotify()
                    if (!waitFor(DEFAULT_TIMEOUT.toLong())) {
                        return null
                    }
                }

                parser?.run {
                    notifyFinished(process.exitValue())
                    return output.takeIf { !isErrorsReported() }
                }
            } catch (ignored: IOException) {
            }
        }

        return null
    }
}
