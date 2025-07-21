package co.infinum.princeofversions

import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor

internal class PresenterImpl(
    private val interactor: Interactor,
    private val storage: Storage
) : Presenter {

    @Throws(Throwable::class)
    override fun check(loader: Loader, appConfig: ApplicationConfiguration): UpdateResult {
        return run(loader, appConfig)
    }

    override fun check(
        loader: Loader,
        executor: Executor,
        callback: UpdaterCallback,
        appConfig: ApplicationConfiguration
    ): PrinceOfVersionsCancelable {
        val call = createCall()
        executor.execute {
            try {
                val result = run(loader, appConfig)
                if (!call.isCanceled) {
                    callback.onSuccess(result)
                }
            } catch (t: Throwable) {
                if (!call.isCanceled) {
                    callback.onError(t)
                }
            }
        }
        return call
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    @Throws(Throwable::class)
    fun run(loader: Loader, appConfig: ApplicationConfiguration): UpdateResult {
        val result = interactor.check(loader, appConfig)

        when (result.status) {
            UpdateStatus.REQUIRED_UPDATE_NEEDED -> {
                storage.rememberLastNotifiedVersion(result.updateVersion)
                return UpdateResult(result.info, result.metadata, UpdateStatus.REQUIRED_UPDATE_NEEDED, result.updateVersion)
            }
            UpdateStatus.NEW_UPDATE_AVAILABLE -> {
                val lastNotifiedVersion = storage.lastNotifiedVersion(null)
                val isNotified = lastNotifiedVersion != null && lastNotifiedVersion == result.updateVersion
                val shouldNotify = !isNotified || result.safeNotificationType() == NotificationType.ALWAYS

                if (shouldNotify) {
                    storage.rememberLastNotifiedVersion(result.updateVersion)
                    return UpdateResult(result.info, result.metadata, UpdateStatus.NEW_UPDATE_AVAILABLE, result.updateVersion)
                }
                // If not notifying, fall through to the NO_UPDATE_AVAILABLE case
            }
            UpdateStatus.NO_UPDATE_AVAILABLE -> {
                // Explicitly do nothing to fall through to the default case
            }
        }

        // Default case for NO_UPDATE_AVAILABLE or a NEW_UPDATE_AVAILABLE that shouldn't be notified
        return UpdateResult(result.info, result.metadata, UpdateStatus.NO_UPDATE_AVAILABLE, result.updateVersion)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun createCall(): PrinceOfVersionsCancelable = UpdaterCancelable()
}
