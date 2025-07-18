package co.infinum.princeofversions

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * An implementation of [Executor] that runs the update check on a single background thread.
 */
internal class PrinceOfVersionsDefaultExecutor : Executor {

    private val service = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "PrinceOfVersions Thread").apply {
            // Set as a daemon thread so it doesn't prevent the JVM from exiting.
            isDaemon = true
        }
    }

    override fun execute(runnable: Runnable) {
        service.submit(runnable)
    }
}
