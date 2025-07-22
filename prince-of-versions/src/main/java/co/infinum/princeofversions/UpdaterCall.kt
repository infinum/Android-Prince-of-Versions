package co.infinum.princeofversions

import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * An invocation of a [PrinceOfVersions] update check method.
 * The instance can be used only once; if a call is already executed or enqueued, a new instance should be used instead.
 *
 * Calls may be executed synchronously with [execute] or asynchronously with [enqueue].
 * In either case, the call can be canceled at any time with [cancel].
 */
internal class UpdaterCall(
    private val core: PrinceOfVersions,
    private val loader: Loader
) : PrinceOfVersionsCall {

    companion object {
        private const val CANCELED_MESSAGE = "Canceled!"
        private const val ALREADY_EXECUTED_MESSAGE = "Already executed!"
    }

    private val executed = AtomicBoolean(false)
    private val canceled = AtomicBoolean(false)

    private var cancelable: PrinceOfVersionsCancelable? = null

    @Throws(Throwable::class)
    override fun execute(): UpdateResult {
        check(!(executed.getAndSet(true))) { ALREADY_EXECUTED_MESSAGE }
        if (isCanceled) {
            throw IOException(CANCELED_MESSAGE)
        }
        return core.checkForUpdates(loader)
    }

    override fun enqueue(callback: UpdaterCallback) {
        check(!(executed.getAndSet(true))) { ALREADY_EXECUTED_MESSAGE }
        if (isCanceled) {
            callback.onError(IOException(CANCELED_MESSAGE))
            return
        }
        cancelable = core.checkForUpdates(loader, callback)
    }

    override fun enqueue(executor: Executor, callback: UpdaterCallback) {
        check(!(executed.getAndSet(true))) { ALREADY_EXECUTED_MESSAGE }
        if (isCanceled) {
            callback.onError(IOException(CANCELED_MESSAGE))
            return
        }
        cancelable = core.checkForUpdates(executor, loader, callback)
    }

    override fun cancel() {
        canceled.set(true)
        cancelable?.cancel()
    }

    override val isCanceled: Boolean
        get() = canceled.get()
}