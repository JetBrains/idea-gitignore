// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.util

import com.intellij.concurrency.JobScheduler
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.DumbAwareRunnable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Debounced runnable class that allows to run command just once in case it was triggered to often.
 */
abstract class Debounced<T>(private val delay: Int) : DumbAwareRunnable, Disposable {

    /** Timer that depends on the given [.delay] value. */
    private var timer: ScheduledFuture<*>? = null

    /** Wrapper run() method to invoke [.timer] properly. */
    override fun run() {
        run(null)
    }

    /** Wrapper run() method to invoke [.timer] properly. */
    fun run(argument: T?) {
        timer?.cancel(false)
        timer = JobScheduler.getScheduler().schedule(
            DumbAwareRunnable { task(argument) },
            delay.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    /** Task to run in debounce way. */
    protected abstract fun task(argument: T?)

    override fun dispose() {
        timer?.cancel(true)
    }
}
