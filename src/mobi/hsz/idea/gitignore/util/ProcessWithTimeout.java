/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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

package mobi.hsz.idea.gitignore.util;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.3.2
 */
public class ProcessWithTimeout extends Thread {
    private Process process;
    private int exitCode = Integer.MIN_VALUE;

    public ProcessWithTimeout(Process process) {
        this.process = process;
    }

    public int waitForProcess(int p_timeoutMilliseconds) {
        this.start();

        try {
            this.join(p_timeoutMilliseconds);
        } catch (InterruptedException e) {
            this.interrupt();
        }

        return exitCode;
    }

    @Override
    public void run() {
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException ignore) {
        } catch (Exception ignored) {
        }
    }
}