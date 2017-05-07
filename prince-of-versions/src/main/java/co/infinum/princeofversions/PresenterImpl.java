package co.infinum.princeofversions;

import android.support.annotation.VisibleForTesting;

import static co.infinum.princeofversions.UpdateStatus.MANDATORY;

public class PresenterImpl implements Presenter {

    private Interactor interactor;

    private Storage storage;

    public PresenterImpl(Interactor interactor, Storage storage) {
        this.interactor = interactor;
        this.storage = storage;
    }

    @Override
    public Result check(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        return run(loader, appConfig);
    }

    @Override
    public PrinceOfVersionsCall check(final Loader loader, Executor executor, final UpdaterCallback callback,
            final ApplicationConfiguration appConfig) {
        final PrinceOfVersionsCall call = createCall();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result result = PresenterImpl.this.run(loader, appConfig);
                    if (!call.isCanceled()) {
                        switch (result.getStatus() != null ? result.getStatus() : UpdateStatus.NO_UPDATE) {
                            case MANDATORY:
                                callback.onNewUpdate(result.getVersion(), true, result.getMetadata());
                                break;
                            case OPTIONAL:
                                callback.onNewUpdate(result.getVersion(), false, result.getMetadata());
                                break;
                            case NO_UPDATE:
                                callback.onNoUpdate(result.getMetadata());
                            default:
                        }
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
    public Result run(Loader loader, ApplicationConfiguration appConfig) throws Throwable {
        CheckResult result = interactor.check(loader, appConfig);
        switch (result.status() != null ? result.status() : UpdateStatus.NO_UPDATE) {
            case MANDATORY:
                storage.rememberLastNotifiedVersion(result.getUpdateVersion());
                return new Result(MANDATORY, result.getUpdateVersion(), result.metadata());
            case OPTIONAL:
                String lastNotifiedVersion = storage.lastNotifiedVersion(null);
                boolean notNotifiedUpdateAvailable = lastNotifiedVersion == null || !lastNotifiedVersion.equals(result.getUpdateVersion());
                boolean alreadyNotifiedUpdateAvailable = lastNotifiedVersion != null && lastNotifiedVersion
                        .equals(result.getUpdateVersion());
                if (notNotifiedUpdateAvailable || (alreadyNotifiedUpdateAvailable && NotificationType.ALWAYS
                        .equals(result.getNotificationType()))) {
                    storage.rememberLastNotifiedVersion(result.getUpdateVersion());
                    return new Result(UpdateStatus.OPTIONAL, result.getUpdateVersion(), result.metadata());
                }
            case NO_UPDATE:
            default:
                return new Result(UpdateStatus.NO_UPDATE, result.getUpdateVersion(), result.metadata());
        }
    }

    @VisibleForTesting
    public PrinceOfVersionsCall createCall() {
        return new UpdaterCall();
    }
}