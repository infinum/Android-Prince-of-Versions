package co.infinum.princeofversions;

import android.support.annotation.VisibleForTesting;

import java.util.concurrent.Executor;

import static co.infinum.princeofversions.UpdateStatus.NEW_UPDATE_AVAILABLE;
import static co.infinum.princeofversions.UpdateStatus.NO_UPDATE_AVAILABLE;
import static co.infinum.princeofversions.UpdateStatus.REQUIRED_UPDATE_NEEDED;

class PresenterImpl implements Presenter {

    private Interactor interactor;

    private Storage storage;

    PresenterImpl(Interactor interactor, Storage storage) {
        this.interactor = interactor;
        this.storage = storage;
    }

    @Override
    public UpdateResult check(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        return run(loader, appConfig);
    }

    @Override
    public PrinceOfVersionsCancelable check(final Loader loader, Executor executor, final UpdaterCallback callback,
        final ApplicationConfiguration appConfig) {
        final PrinceOfVersionsCancelable call = createCall();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateResult result = PresenterImpl.this.run(loader, appConfig);
                    if (!call.isCanceled()) {
                        callback.onSuccess(result);
                    }
                } catch (Throwable t) {
                    if (!call.isCanceled()) {
                        callback.onError(t);
                    }
                }
            }
        });
        return call;
    }

    @VisibleForTesting
    UpdateResult run(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        CheckResult result = interactor.check(loader, appConfig);
        switch (result.status()) {
            case REQUIRED_UPDATE_NEEDED:
                storage.rememberLastNotifiedVersion(result.getUpdateVersion());
                return new UpdateResult(result.getInfo(), result.metadata(), REQUIRED_UPDATE_NEEDED, result.getUpdateVersion());
            case NEW_UPDATE_AVAILABLE:
                Integer lastNotifiedVersion = storage.lastNotifiedVersion(null);
                boolean notNotifiedUpdateAvailable = lastNotifiedVersion == null || !lastNotifiedVersion.equals(result.getUpdateVersion());
                boolean alreadyNotifiedUpdateAvailable =
                    lastNotifiedVersion != null && lastNotifiedVersion.equals(result.getUpdateVersion());
                if (notNotifiedUpdateAvailable || (
                    alreadyNotifiedUpdateAvailable && NotificationType.ALWAYS.equals(result.getNotificationType())
                )) {
                    storage.rememberLastNotifiedVersion(result.getUpdateVersion());
                    return new UpdateResult(result.getInfo(), result.metadata(), NEW_UPDATE_AVAILABLE, result.getUpdateVersion());
                }
            case NO_UPDATE_AVAILABLE:
            default:
                return new UpdateResult(result.getInfo(), result.metadata(), NO_UPDATE_AVAILABLE, result.getUpdateVersion());
        }
    }

    @VisibleForTesting
    PrinceOfVersionsCancelable createCall() {
        return new UpdaterCancelable();
    }
}
