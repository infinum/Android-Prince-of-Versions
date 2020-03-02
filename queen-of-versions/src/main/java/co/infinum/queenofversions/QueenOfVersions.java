package co.infinum.queenofversions;

import android.app.Activity;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.PrinceOfVersionsCancelable;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.princeofversions.UpdaterCallback;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class QueenOfVersions {

    private static final int DEFAULT_REQUEST_CODE = 128;

    private final Storage storage;

    private final OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

    private final OnPrinceOfVersionsError onPrinceOfVersionsError;

    private final int requestCode;

    private final Activity activity;

    private final PrinceOfVersions princeOfVersions;

    QueenOfVersions(
            Storage storage,
            OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess,
            OnPrinceOfVersionsError onPrinceOfVersionsError,
            int requestCode,
            Activity activity,
            PrinceOfVersions princeOfVersions
    ) {
        this.storage = storage;
        this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
        this.onPrinceOfVersionsError = onPrinceOfVersionsError;
        this.requestCode = requestCode;
        this.activity = activity;
        this.princeOfVersions = princeOfVersions;
    }

    public static PrinceOfVersionsCancelable checkForUpdates(
            Activity activity,
            QueenOfVersions.Options options,
            QueenOfVersions.Callback callback
    ) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                options.requestCode,
                activity,
                callback,
                QueenOnPrinceOfVersionsSuccess.INSTANCE,
                QueenOnPrinceOfVersionsError.INSTANCE,
                options.storage != null ? options.storage : new QueenOfVersionsDefaultNamedPreferenceStorage(activity)
        );
        UpdateStatus updateStatus = options.updateStatus;
        UpdateResult updateResult = options.updateResult;

        if (updateStatus != null) {
            updater.continueUpdateCheckBasedOnStatus(updateStatus, updateResult);
        } else {
            if (updateResult != null) {
                updater.onSuccess(updateResult);
            } else {
                updater.continueUpdateCheckBasedOnStatus(UpdateStatus.NEW_UPDATE_AVAILABLE, updateResult);
            }
        }

        return new QueenOfVersionsCancelable(updater);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link String}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param url      Url from where update config will be loaded.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(String url, QueenOfVersions.Callback callback) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                requestCode, activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, storage
        );
        PrinceOfVersionsCancelable princeOfVersionsCancelable = princeOfVersions.checkForUpdates(url, updater);
        return new QueenOfVersionsCancelable(updater, princeOfVersionsCancelable);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link Loader}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param loader   Instance for loading update config resource.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Loader loader, QueenOfVersions.Callback callback) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                requestCode, activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, storage
        );
        PrinceOfVersionsCancelable princeOfVersionsCancelable = princeOfVersions.checkForUpdates(loader, updater);
        return new QueenOfVersionsCancelable(updater, princeOfVersionsCancelable);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link String}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param executor Instance for running check call.
     * @param url      Url from where update config will be loaded.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Executor executor, String url, QueenOfVersions.Callback callback) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                requestCode, activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, storage
        );
        PrinceOfVersionsCancelable princeOfVersionsCancelable = princeOfVersions.checkForUpdates(executor, url, updater);
        return new QueenOfVersionsCancelable(updater, princeOfVersionsCancelable);
    }

    /**
     * Start asynchronous check for update using provided {@link Executor} and {@link Loader}. Notifies result to provided {@link
     * UpdaterCallback}.
     *
     * @param executor Instance for running check call.
     * @param loader   Instance for loading update config resource.
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the call.
     */
    public PrinceOfVersionsCancelable checkForUpdates(Executor executor, Loader loader, QueenOfVersions.Callback callback) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                requestCode, activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, storage
        );
        PrinceOfVersionsCancelable princeOfVersionsCancelable = princeOfVersions.checkForUpdates(executor, loader, updater);
        return new QueenOfVersionsCancelable(updater, princeOfVersionsCancelable);
    }

    public interface Callback {

        Callback DEFAULT = new Callback() {
            @Override
            public void onDownloaded(QueenOfVersions.UpdateHandler handler) {
                handler.completeUpdate();
            }

            @Override
            public void onCanceled() {
                // no-op
            }

            @Override
            public void onInstalled() {
                // no-op
            }

            @Override
            public void onPending() {
                // no-op
            }

            @Override
            public void onError(Throwable throwable) {
                // no-op
            }

            @Override
            public void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo) {
                // no-op
            }

            @Override
            public void onDownloading() {
                // no-op
            }

            @Override
            public void onInstalling() {
                // no-op
            }

            @Override
            public void onMandatoryUpdateNotAvailable(
                    int mandatoryVersion,
                    int availableVersion,
                    Map<String, String> metadata,
                    UpdateInfo updateInfo
            ) {
                onNoUpdate(metadata, updateInfo);
            }
        };

        void onDownloaded(QueenOfVersions.UpdateHandler handler);

        void onCanceled();

        void onInstalled();

        void onPending();

        /**
         * This method is called if there is an unknown behaviour of the update. This can happen if there was an update available,
         * but during the updating something was unknown or there was some kind of unreported error.
         */
        void onError(Throwable throwable);

        void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);

        void onDownloading();

        void onInstalling();

        void onMandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion, Map<String, String> metadata, UpdateInfo updateInfo);

        class Builder {

            private CallbackAdapter adapter = new CallbackAdapter();

            public QueenOfVersions.Callback.Builder withCallback(QueenOfVersions.Callback callback) {
                adapter.withFallback(callback);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnDownloaded(OnDownloaded action) {
                adapter.withOnDownloaded(action);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnCanceled(Runnable onCanceled) {
                adapter.withOnCanceled(onCanceled);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnInstalled(Runnable onInstalled) {
                adapter.withOnInstalled(onInstalled);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnPending(Runnable onPending) {
                adapter.withOnPending(onPending);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnError(OnError onError) {
                adapter.withOnError(onError);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnNoUpdate(OnNoUpdate onNoUpdate) {
                adapter.withOnNoUpdate(onNoUpdate);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnDownloading(Runnable onDownloading) {
                adapter.withOnDownloading(onDownloading);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnInstalling(Runnable onInstalling) {
                adapter.withOnInstalling(onInstalling);
                return this;
            }

            public QueenOfVersions.Callback.Builder withOnMandatoryUpdateNotAvailable(
                    OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable) {
                adapter.withOnMandatoryUpdateNotAvailable(onMandatoryUpdateNotAvailable);
                return this;
            }

            public QueenOfVersions.Callback build() {
                return adapter.copy();
            }
        }
    }

    public interface OnDownloaded {

        void onAction(QueenOfVersions.UpdateHandler handler);
    }

    public interface OnError {

        void onError(Throwable error);
    }

    public interface OnMandatoryUpdateNotAvailable {

        void onAction(int mandatoryVersion, int availableVersion, Map<String, String> metadata, UpdateInfo updateInfo);
    }

    public interface OnNoUpdate {

        void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo);
    }

    public interface UpdateHandler {

        void completeUpdate();
    }

    static class QueenOnPrinceOfVersionsSuccess implements OnPrinceOfVersionsSuccess {

        static final QueenOnPrinceOfVersionsSuccess INSTANCE = new QueenOnPrinceOfVersionsSuccess();

        @Override
        public UpdateStatus handleUpdateResultAsStatus(UpdateResult result) {
            UpdateStatus status = result.getStatus();
            if (status == UpdateStatus.NO_UPDATE_AVAILABLE) {
                return UpdateStatus.NEW_UPDATE_AVAILABLE;
            } else {
                return status;
            }
        }
    }

    static class QueenOnPrinceOfVersionsError implements OnPrinceOfVersionsError {

        static final QueenOnPrinceOfVersionsError INSTANCE = new QueenOnPrinceOfVersionsError();

        @Override
        public UpdateStatus continueUpdateCheckAsStatus(Throwable error) {
            return UpdateStatus.NEW_UPDATE_AVAILABLE;
        }
    }

    public static class Builder {

        private int requestCode = DEFAULT_REQUEST_CODE;

        @Nullable
        private Storage storage = null;

        @Nullable
        private OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

        @Nullable
        private OnPrinceOfVersionsError onPrinceOfVersionsError;

        @Nullable
        private PrinceOfVersions princeOfVersions;

        public Builder withRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder withPrinceOfVersionsSuccessHandler(OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess) {
            this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
            return this;
        }

        public Builder withPrinceOfVersionsErrorHandler(OnPrinceOfVersionsError onPrinceOfVersionsError) {
            this.onPrinceOfVersionsError = onPrinceOfVersionsError;
            return this;
        }

        public Builder withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public Builder withPrinceOfVersions(PrinceOfVersions princeOfVersions) {
            this.princeOfVersions = princeOfVersions;
            return this;
        }

        public QueenOfVersions build(Activity activity) {
            return new QueenOfVersions(
                    storage != null ? storage : new QueenOfVersionsDefaultNamedPreferenceStorage(activity),
                    onPrinceOfVersionsSuccess != null ? onPrinceOfVersionsSuccess : new QueenOnPrinceOfVersionsSuccess(),
                    onPrinceOfVersionsError != null ? onPrinceOfVersionsError : new QueenOnPrinceOfVersionsError(),
                    requestCode,
                    activity,
                    princeOfVersions != null ? princeOfVersions : new PrinceOfVersions(activity)
            );
        }
    }

    public static class Options {

        private int requestCode = DEFAULT_REQUEST_CODE;

        @Nullable
        private Storage storage;

        @Nullable
        private UpdateStatus updateStatus;

        @Nullable
        private UpdateResult updateResult;

        private Options(
                int requestCode,
                @Nullable Storage storage,
                @Nullable UpdateStatus updateStatus,
                @Nullable UpdateResult updateResult
        ) {
            this.requestCode = requestCode;
            this.storage = storage;
            this.updateStatus = updateStatus;
            this.updateResult = updateResult;
        }

        public Options withRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Options withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        public Options withUpdateStatus(UpdateStatus updateStatus) {
            this.updateStatus = updateStatus;
            return this;
        }

        public Options withUpdateResult(UpdateResult updateResult) {
            this.updateResult = updateResult;
            return this;
        }

        public Options build() {
            return new Options(
                    requestCode,
                    storage,
                    updateStatus,
                    updateResult
            );
        }
    }

    static class CallbackAdapter implements QueenOfVersions.Callback {

        private Callback fallback;

        @Nullable
        private OnDownloaded onDownloaded;

        @Nullable
        private Runnable onCanceled;

        @Nullable
        private Runnable onInstalled;

        @Nullable
        private Runnable onPending;

        @Nullable
        private OnError onError;

        @Nullable
        private OnNoUpdate onNoUpdate;

        @Nullable
        private Runnable onDownloading;

        @Nullable
        private Runnable onInstalling;

        @Nullable
        private OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable;

        CallbackAdapter(Callback fallback) {
            this.fallback = fallback;
        }

        CallbackAdapter() {
            this(Callback.DEFAULT);
        }

        void withFallback(Callback callback) {
            this.fallback = callback;
        }

        void withOnDownloaded(OnDownloaded action) {
            this.onDownloaded = action;
        }

        void withOnCanceled(Runnable onCanceled) {
            this.onCanceled = onCanceled;
        }

        void withOnInstalled(Runnable onInstalled) {
            this.onInstalled = onInstalled;
        }

        void withOnPending(Runnable onPending) {
            this.onPending = onPending;
        }

        void withOnError(OnError onError) {
            this.onError = onError;
        }

        void withOnNoUpdate(OnNoUpdate onNoUpdate) {
            this.onNoUpdate = onNoUpdate;
        }

        void withOnDownloading(Runnable onDownloading) {
            this.onDownloading = onDownloading;
        }

        void withOnInstalling(Runnable onInstalling) {
            this.onInstalling = onInstalling;
        }

        void withOnMandatoryUpdateNotAvailable(OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable) {
            this.onMandatoryUpdateNotAvailable = onMandatoryUpdateNotAvailable;
        }

        @Override
        public void onDownloaded(QueenOfVersions.UpdateHandler handler) {
            if (onDownloaded != null) {
                onDownloaded.onAction(handler);
            } else {
                fallback.onDownloaded(handler);
            }
        }

        @Override
        public void onCanceled() {
            if (onCanceled != null) {
                onCanceled.run();
            } else {
                fallback.onCanceled();
            }
        }

        @Override
        public void onInstalled() {
            if (onInstalled != null) {
                onInstalled.run();
            } else {
                fallback.onInstalled();
            }
        }

        @Override
        public void onPending() {
            if (onPending != null) {
                onPending.run();
            } else {
                fallback.onPending();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (onError != null) {
                onError.onError(throwable);
            } else {
                fallback.onError(throwable);
            }
        }

        @Override
        public void onNoUpdate(@Nullable Map<String, String> metadata, @Nullable UpdateInfo updateInfo) {
            if (onNoUpdate != null) {
                onNoUpdate.onNoUpdate(metadata, updateInfo);
            } else {
                fallback.onNoUpdate(metadata, updateInfo);
            }
        }

        @Override
        public void onDownloading() {
            if (onDownloading != null) {
                onDownloading.run();
            } else {
                fallback.onDownloading();
            }
        }

        @Override
        public void onInstalling() {
            if (onInstalling != null) {
                onInstalling.run();
            } else {
                fallback.onInstalling();
            }
        }

        @Override
        public void onMandatoryUpdateNotAvailable(
                int mandatoryVersion,
                int availableVersion,
                Map<String, String> metadata,
                UpdateInfo updateInfo
        ) {
            if (onMandatoryUpdateNotAvailable != null) {
                onMandatoryUpdateNotAvailable.onAction(mandatoryVersion, availableVersion, metadata, updateInfo);
            } else {
                fallback.onMandatoryUpdateNotAvailable(mandatoryVersion, availableVersion, metadata, updateInfo);
            }
        }

        CallbackAdapter copy() {
            CallbackAdapter copy = new CallbackAdapter(fallback);
            if (onInstalling != null) {
                copy.withOnInstalling(onInstalling);
            }
            if (onMandatoryUpdateNotAvailable != null) {
                copy.withOnMandatoryUpdateNotAvailable(onMandatoryUpdateNotAvailable);
            }
            if (onDownloading != null) {
                copy.withOnDownloading(onDownloading);
            }
            if (onError != null) {
                copy.withOnError(onError);
            }
            if (onPending != null) {
                copy.withOnPending(onPending);
            }
            if (onInstalled != null) {
                copy.withOnInstalled(onInstalled);
            }
            if (onCanceled != null) {
                copy.withOnCanceled(onCanceled);
            }
            if (onDownloaded != null) {
                copy.withOnDownloaded(onDownloaded);
            }
            if (onNoUpdate != null) {
                copy.withOnNoUpdate(onNoUpdate);
            }
            return copy;
        }
    }

    public static class UnknownVersionException extends Exception {

    }
}
