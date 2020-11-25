// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.DumbAwareRunnable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Wrapper for [JobScheduler] that runs a scheduled operation [.maxAttempts] times every [.delay]
 * milliseconds. It is possible to manually break scheduled task with calling [.cancel].
 */
class InterruptibleScheduledFuture(private val task: Runnable, private val delay: Int, private val maxAttempts: Int) :
    DumbAwareRunnable, Disposable {

    /** Invocations counter.  */
    private var attempt = 0

    /** Current scheduled feature.  */
    private var future: ScheduledFuture<*>? = null

    /** Specify invoking on the leading edge of the timeout.  */
    private var leading = false

    /** Specify invoking on the trailing edge of the timeout.  */
    private var trailing = false

    /** Flag to distinguish the trailing task.  */
    private var trailingTask = false

    /** Main run function.  */
    override fun run() {
        if (future != null && !future!!.isCancelled && !future!!.isDone) {
            return
        }
        if (leading) {
            task.run()
        }
        future = JobScheduler.getScheduler().scheduleWithFixedDelay(
            {
                task.run()
                if (++attempt >= maxAttempts || trailingTask) {
                    trailing = false
                    if (future != null) {
                        future!!.cancel(false)
                    }
                }
            },
            delay.toLong(),
            delay.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    /** Function that cancels current [.future].  */
    fun cancel() {
        if (future != null && !future!!.isCancelled) {
            if (trailing) {
                trailingTask = true
            } else {
                future!!.cancel(true)
            }
        }
    }

    /**
     * Specify invoking on the leading edge of the timeout.
     *
     * @param leading edge
     */
    fun setLeading(leading: Boolean) {
        this.leading = leading
    }

    /**
     * Specify invoking on the trailing edge of the timeout.
     *
     * @param trailing edge
     */
    fun setTrailing(trailing: Boolean) {
        this.trailing = trailing
    }

    override fun dispose() = cancel()
}
