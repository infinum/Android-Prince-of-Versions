package co.infinum.princeofversions

import java.util.concurrent.Executor

/**
 * This class wraps [UpdaterCallback] instance to run all methods on specific executor.
 *
 * @param callback Instance to which calls will be delegated.
 * @param executor Executor which will execute the delegation to the real callback
 */
internal class ExecutorUpdaterCallback(
    private val callback: UpdaterCallback,
    private val executor: Executor
) : UpdaterCallback {

    override fun onSuccess(result: UpdateResult) {
        executor.execute {
            callback.onSuccess(result)
        }
    }

    override fun onError(error: Throwable) {
        executor.execute {
            callback.onError(error)
        }
    }
}
