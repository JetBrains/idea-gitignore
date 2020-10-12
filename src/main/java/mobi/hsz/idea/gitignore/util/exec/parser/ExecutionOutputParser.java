/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.util.exec.parser;

import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Abstract output parser for the ExternalExec process outputs.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public abstract class ExecutionOutputParser<T> {
    /** Outputs list. */
    @NotNull
    private final ArrayList<T> outputs = new ArrayList<>();

    /** Exit code value. */
    private int exitCode;

    /** Error occurred during the output parsing. */
    private boolean errorsReported;

    /**
     * Handles single output line.
     *
     * @param text       execution response
     * @param outputType output type
     */
    public void onTextAvailable(@NotNull String text, @NotNull Key outputType) {
        if (outputType == ProcessOutputTypes.SYSTEM) {
            return;
        }

        if (outputType == ProcessOutputTypes.STDERR) {
            errorsReported = true;
            return;
        }

        ContainerUtil.addIfNotNull(outputs, parseOutput(StringUtil.trimEnd(text, "\n").trim()));
    }

    /**
     * Main method that parses output for the specified result data.
     *
     * @param text input data
     * @return single parsed result
     */
    @Nullable
    protected abstract T parseOutput(@NotNull String text);

    /**
     * Method called at the end of the parsing process.
     *
     * @param exitCode result of the executable call
     */
    public void notifyFinished(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Returns collected output.
     *
     * @return parsed output
     */
    @Nullable
    public ArrayList<T> getOutput() {
        return outputs;
    }

    /**
     * Checks if any error occurred during the parsing.
     *
     * @return error was reported
     */
    public boolean isErrorsReported() {
        return errorsReported || exitCode != 0;
    }
}
