package co.infinum.princeofversions

import java.util.concurrent.Executor

/**
 * An invocation of a [PrinceOfVersions] update check method.
 * The instance can be used only once, if call is already executed or enqueued new instnace should be used instead.
 *
 *
 * Calls may be executed synchronously with [.execute], or asynchronously with [.enqueue].
 * In either case the call can be canceled at any time with [.cancel].
 */
interface PrinceOfVersionsCall : PrinceOfVersionsCancelable {
    /**
     * Synchronously execute the update check and return its result.
     *
     * @return Result containing information about new update
     * @throws Throwable In case of any error during the update check. [java.io.IOException] would signal that fetching
     * configuration content, while any other error would signal either invalid update configuration or inability to
     * compare versions correctly
     */
    @Throws(Throwable::class)
    fun execute(): UpdateResult?

    /**
     * Asynchronously starts the update check and notify `callback` of its response or if an error
     * occurred during the process.
     *
     * @param callback To be enqueued when update check finishes.
     */
    fun enqueue(callback: UpdaterCallback)

    /**
     * Asynchronously starts the update check and notify `callback` of its response or if an error
     * occurred during the process.
     *
     * @param executor executes the update check
     * @param callback To be enqueued when update check finishes.
     */
    fun enqueue(executor: Executor, callback: UpdaterCallback)
}
