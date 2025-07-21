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

    private val executed = AtomicBoolean(false)
    private val canceled = AtomicBoolean(false)

    private var cancelable: PrinceOfVersionsCancelable? = null

    @Throws(Throwable::class)
    override fun execute(): UpdateResult {
        if (executed.getAndSet(true)) {
            throw IllegalStateException("Already executed!")
        }
        if (isCanceled) {
            throw IOException("Canceled!")
        }
        return core.checkForUpdates(loader)
    }

    override fun enqueue(callback: UpdaterCallback) {
        if (executed.getAndSet(true)) {
            throw IllegalStateException("Already executed!")
        }
        if (isCanceled) {
            callback.onError(IOException("Canceled!"))
            return
        }
        cancelable = core.checkForUpdates(loader, callback)
    }

    override fun enqueue(executor: Executor, callback: UpdaterCallback) {
        if (executed.getAndSet(true)) {
            throw IllegalStateException("Already executed!")
        }
        if (isCanceled) {
            callback.onError(IOException("Canceled!"))
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