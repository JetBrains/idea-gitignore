// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util.exec.parser

import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.containers.addIfNotNull

/**
 * Abstract output parser for the ExternalExec process outputs.
 */
abstract class ExecutionOutputParser<T> {

    /** Outputs list.  */
    private val outputs = mutableListOf<T>()

    /** Exit code value.  */
    private var exitCode = 0

    /** Error occurred during the output parsing.  */
    private var errorsReported = false

    /**
     * Handles single output line.
     *
     * @param text       execution response
     * @param outputType output type
     */
    fun onTextAvailable(text: String, outputType: Key<*>) {
        if (outputType === ProcessOutputTypes.SYSTEM) {
            return
        }
        if (outputType === ProcessOutputTypes.STDERR) {
            errorsReported = true
            return
        }
        outputs.addIfNotNull(parseOutput(StringUtil.trimEnd(text, "\n").trim { it <= ' ' }))
    }

    /**
     * Main method that parses output for the specified result data.
     *
     * @param text input data
     * @return single parsed result
     */
    protected abstract fun parseOutput(text: String): T?

    /**
     * Method called at the end of the parsing process.
     *
     * @param exitCode result of the executable call
     */
    fun notifyFinished(exitCode: Int) {
        this.exitCode = exitCode
    }

    /**
     * Returns collected output.
     *
     * @return parsed output
     */
    val output: List<T>
        get() = outputs

    /**
     * Checks if any error occurred during the parsing.
     *
     * @return error was reported
     */
    fun isErrorsReported() = errorsReported || exitCode != 0
}
