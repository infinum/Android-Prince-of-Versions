package co.infinum.princeofversions;

public class PresenterImpl implements Presenter {

    private Interactor interactor;

    private Storage storage;

    private ApplicationConfiguration appConfig;

    public PresenterImpl(Interactor interactor, Storage storage, ApplicationConfiguration appConfig) {
        this.interactor = interactor;
        this.storage = storage;
        this.appConfig = appConfig;
    }

    @Override
    public Result check(Loader loader) throws Throwable {
        return run(loader);
    }

    @Override
    public PrinceOfVersionsCall check(final Loader loader, Executor executor, final UpdaterCallback callback) {
        final PrinceOfVersionsCall call = new UpdaterCall();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result result = PresenterImpl.this.run(loader);
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

    private Result run(Loader loader) throws Throwable {
        CheckResult result = interactor.check(loader, appConfig);
        switch (result.status() != null ? result.status() : UpdateStatus.NO_UPDATE) {
            case MANDATORY:
                storage.rememberLastNotifiedVersion(result.getUpdateVersion());
                return new Result(UpdateStatus.MANDATORY, result.getUpdateVersion(), result.metadata());
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
}
