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

package mobi.hsz.idea.gitignore.command;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.7
 */
public abstract class CommandAction<T> {
    /** Current project. */
    @NotNull
    private final Project project;

    /**
     * Constructor.
     *
     * @param project current project
     */
    public CommandAction(@NotNull Project project) {
        this.project = project;
    }

    protected abstract T compute() throws Throwable;

    public final T execute() throws Throwable {
        return WriteCommandAction.writeCommandAction(project).compute(CommandAction.this::compute);
    }
}
