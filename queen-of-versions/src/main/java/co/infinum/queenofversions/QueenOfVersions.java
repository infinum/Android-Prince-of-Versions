package co.infinum.queenofversions;

import android.support.v4.app.FragmentActivity;
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

/**
 * This class represents main entry point for using library.
 * <p>
 * Most common way to create instance of this class should be using {@link QueenOfVersions.Builder}.
 * </p>
 * <p>
 * To check if update exists you can use two different approaches: with or without {@link PrinceOfVersions}.
 * </p>
 * <p>
 * Depending on used approach there are several versions of checkForUpdates method.
 * If using {@link PrinceOfVersions} there are several {@code checkForUpdates} methods available after creating instance of this class.
 * Otherwise there is a static {@code checkForUpdates} method used to start an In-App update check without need for {@link
 * PrinceOfVersions}
 * behavior.
 * All methods return {@link PrinceOfVersionsCancelable} object which you can use to cancel request.
 * </p>
 * <p>
 * Here is code for most common usage of this library
 * <pre>
 *         {@link QueenOfVersions} updater = new {@link QueenOfVersions.Builder}().build();
 *         {@link PrinceOfVersionsCancelable} call = updater.checkForUpdates(
 *                  "http://example.com/some/update.json",
 *                  callback
 *         ); // starts checking for update
 * </pre>
 */
public class QueenOfVersions {

    private final Storage storage;

    private final OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

    private final OnPrinceOfVersionsError onPrinceOfVersionsError;

    private final OnUpdateNotAllowed onUpdateNotAllowed;

    private final OnInAppUpdateAvailable onInAppUpdateAvailable;

    private final FragmentActivity activity;

    private final PrinceOfVersions princeOfVersions;

    QueenOfVersions(
            Storage storage,
            OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess,
            OnPrinceOfVersionsError onPrinceOfVersionsError,
            OnUpdateNotAllowed onUpdateNotAllowed,
            OnInAppUpdateAvailable onInAppUpdateAvailable,
            FragmentActivity activity,
            PrinceOfVersions princeOfVersions
    ) {
        this.storage = storage;
        this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
        this.onPrinceOfVersionsError = onPrinceOfVersionsError;
        this.onUpdateNotAllowed = onUpdateNotAllowed;
        this.onInAppUpdateAvailable = onInAppUpdateAvailable;
        this.activity = activity;
        this.princeOfVersions = princeOfVersions;
    }

    /**
     * Start asynchronous check for update without {@link PrinceOfVersions} check.
     * Use {@link Options} to change default behaviors.
     *
     * @param activity Activity to host the update check.
     * @param options  Options for this update check
     * @param callback Callback to notify result.
     * @return instance through which is possible to cancel the operation.
     */
    public static PrinceOfVersionsCancelable checkForUpdates(
            FragmentActivity activity,
            QueenOfVersions.Options options,
            QueenOfVersions.Callback callback
    ) {
        QueenOfVersionsUpdaterCallback updater = new QueenOfVersionsUpdaterCallback(
                activity,
                callback,
                QueenOnPrinceOfVersionsSuccess.INSTANCE,
                QueenOnPrinceOfVersionsError.INSTANCE,
                options.onUpdateNotAllowed,
                options.onInAppUpdateAvailable,
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
                activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, onUpdateNotAllowed, onInAppUpdateAvailable, storage
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
                activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, onUpdateNotAllowed, onInAppUpdateAvailable, storage
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
                activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, onUpdateNotAllowed, onInAppUpdateAvailable, storage
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
                activity, callback, onPrinceOfVersionsSuccess, onPrinceOfVersionsError, onUpdateNotAllowed, onInAppUpdateAvailable, storage
        );
        PrinceOfVersionsCancelable princeOfVersionsCancelable = princeOfVersions.checkForUpdates(executor, loader, updater);
        return new QueenOfVersionsCancelable(updater, princeOfVersionsCancelable);
    }

    /**
     * All callback methods that can be called during an update check process.
     * Implement to handle custom behavior.
     *
     * @see Callback.Builder to change only some callbacks
     */
    public interface Callback extends
            OnCanceled, OnDownloading, OnDownloaded, OnError, OnInstalled, OnInstalling, OnMandatoryUpdateNotAvailable, OnNoUpdate,
            OnPending, OnUpdateAccepted, OnUpdateDeclined {

        /**
         * Use this class to change only some callback behaviors.
         * All methods are optional.
         */
        class Builder {

            private CallbackAdapter adapter = new CallbackAdapter();

            /**
             * Set custom fallback callback. In case no method is override specifically using this builder this fallback will be called.
             *
             * @param callback fallback
             * @return this
             */
            public QueenOfVersions.Callback.Builder withCallback(QueenOfVersions.Callback callback) {
                adapter.withFallback(callback);
                return this;
            }

            /**
             * Set custom {@link OnDownloaded} callback.
             *
             * @param action callback.
             * @return this
             * @see OnDownloaded
             */
            public QueenOfVersions.Callback.Builder withOnDownloaded(OnDownloaded action) {
                adapter.withOnDownloaded(action);
                return this;
            }

            /**
             * Set custom {@link OnCanceled} callback.
             *
             * @param onCanceled callback.
             * @return this
             * @see OnCanceled
             */
            public QueenOfVersions.Callback.Builder withOnCanceled(OnCanceled onCanceled) {
                adapter.withOnCanceled(onCanceled);
                return this;
            }

            /**
             * Set custom {@link OnInstalled} callback.
             *
             * @param onInstalled callback.
             * @return this
             * @see OnInstalled
             */
            public QueenOfVersions.Callback.Builder withOnInstalled(OnInstalled onInstalled) {
                adapter.withOnInstalled(onInstalled);
                return this;
            }

            /**
             * Set custom {@link OnPending} callback.
             *
             * @param onPending callback.
             * @return this
             * @see OnPending
             */
            public QueenOfVersions.Callback.Builder withOnPending(OnPending onPending) {
                adapter.withOnPending(onPending);
                return this;
            }

            /**
             * Set custom {@link OnError} callback.
             *
             * @param onError callback.
             * @return this
             * @see OnError
             */
            public QueenOfVersions.Callback.Builder withOnError(OnError onError) {
                adapter.withOnError(onError);
                return this;
            }

            /**
             * Set custom {@link OnNoUpdate} callback.
             *
             * @param onNoUpdate callback.
             * @return this
             * @see OnNoUpdate
             */
            public QueenOfVersions.Callback.Builder withOnNoUpdate(OnNoUpdate onNoUpdate) {
                adapter.withOnNoUpdate(onNoUpdate);
                return this;
            }

            /**
             * Set custom {@link OnDownloading} callback.
             *
             * @param onDownloading callback.
             * @return this
             * @see OnDownloading
             */
            public QueenOfVersions.Callback.Builder withOnDownloading(OnDownloading onDownloading) {
                adapter.withOnDownloading(onDownloading);
                return this;
            }

            /**
             * Set custom {@link OnInstalling} callback.
             *
             * @param onInstalling callback.
             * @return this
             * @see OnInstalling
             */
            public QueenOfVersions.Callback.Builder withOnInstalling(OnInstalling onInstalling) {
                adapter.withOnInstalling(onInstalling);
                return this;
            }

            /**
             * Set custom {@link OnMandatoryUpdateNotAvailable} handler.
             *
             * @param onMandatoryUpdateNotAvailable handler.
             * @return this
             * @see OnMandatoryUpdateNotAvailable
             */
            public QueenOfVersions.Callback.Builder withOnMandatoryUpdateNotAvailable(
                    OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable
            ) {
                adapter.withOnMandatoryUpdateNotAvailable(onMandatoryUpdateNotAvailable);
                return this;
            }

            /**
             * Set custom {@link OnUpdateAccepted} handler.
             *
             * @param onUpdateAccepted handler.
             * @return this
             * @see OnUpdateAccepted
             */
            public QueenOfVersions.Callback.Builder withOnUpdateAccepted(OnUpdateAccepted onUpdateAccepted) {
                adapter.withOnUpdateAccepted(onUpdateAccepted);
                return this;
            }

            /**
             * Set custom {@link OnUpdateDeclined} handler.
             *
             * @param onUpdateDeclined handler.
             * @return this
             * @see OnUpdateDeclined
             */
            public QueenOfVersions.Callback.Builder withOnUpdateDeclined(OnUpdateDeclined onUpdateDeclined) {
                adapter.withOnUpdateDeclined(onUpdateDeclined);
                return this;
            }

            /**
             * Builds the {@link Callback} with all custom callbacks set.
             *
             * @return instance of callback built using this builder
             */
            public QueenOfVersions.Callback build() {
                return adapter.copy();
            }
        }
    }

    /**
     * Called in case FLEXIBLE update has been downloaded and is ready to be installed.
     * Use this handler to resume with installation.
     */
    public interface UpdateHandler {

        /**
         * Called in case FLEXIBLE update has been downloaded and is ready to be installed.
         * Call this method to resume with update installation.
         */
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

    static class OnUpdateNotAllowedReportNoUpdate implements OnUpdateNotAllowed {

        @Override
        public boolean onImmediateUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult) {
            return false;
        }

        @Override
        public boolean onFlexibleUpdateNotAllowed(QueenOfVersionsInAppUpdateInfo updateInfo, @Nullable UpdateResult updateResult) {
            return false;
        }
    }

    static class OnInAppUpdateAvailableResumeWithCurrentResolution implements OnInAppUpdateAvailable {

        @Override
        public UpdateStatus handleInAppUpdateAsStatus(
                UpdateStatus currentStatus,
                QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                @Nullable UpdateResult updateResult
        ) {
            return currentStatus;
        }
    }

    /**
     * Builds {@link QueenOfVersions} instance. All parameters are optional.
     */
    public static class Builder {

        @Nullable
        private Storage storage = null;

        @Nullable
        private OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

        @Nullable
        private OnPrinceOfVersionsError onPrinceOfVersionsError;

        @Nullable
        private OnUpdateNotAllowed onUpdateNotAllowed;

        @Nullable
        private OnInAppUpdateAvailable onInAppUpdateAvailable;

        @Nullable
        private PrinceOfVersions princeOfVersions;

        /**
         * Set {@link OnPrinceOfVersionsSuccess} handler.
         *
         * @param onPrinceOfVersionsSuccess handler
         * @return this
         * @see OnPrinceOfVersionsSuccess
         */
        public Builder withPrinceOfVersionsSuccessHandler(OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess) {
            this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
            return this;
        }

        /**
         * Set {@link OnPrinceOfVersionsError} handler.
         *
         * @param onPrinceOfVersionsError handler
         * @return this
         * @see OnPrinceOfVersionsError
         */
        public Builder withPrinceOfVersionsErrorHandler(OnPrinceOfVersionsError onPrinceOfVersionsError) {
            this.onPrinceOfVersionsError = onPrinceOfVersionsError;
            return this;
        }

        /**
         * Set custom storage used for saving last notified update, for the sake of not notifying same update twice
         * in case of update with {@link co.infinum.princeofversions.NotificationType} ONCE.
         *
         * @param storage implementation of the storage.
         * @return this
         */
        public Builder withStorage(Storage storage) {
            this.storage = storage;
            return this;
        }

        /**
         * Set {@link PrinceOfVersions} instance used to make an update check.
         *
         * @param princeOfVersions configured instance of {@link PrinceOfVersions}
         * @return this
         */
        public Builder withPrinceOfVersions(PrinceOfVersions princeOfVersions) {
            this.princeOfVersions = princeOfVersions;
            return this;
        }

        /**
         * Use to set handler for not allowed update flow.
         *
         * @param onUpdateNotAllowedHandler implementation of handler
         * @return this
         * @see OnUpdateNotAllowed
         */
        public Builder withOnUpdateNotAllowedHandler(OnUpdateNotAllowed onUpdateNotAllowedHandler) {
            this.onUpdateNotAllowed = onUpdateNotAllowedHandler;
            return this;
        }

        /**
         * Use to set handler for available In-App update.
         *
         * @param onInAppUpdateAvailable implementation of handler
         * @return this
         * @see OnInAppUpdateAvailable
         */
        public Builder withOnInAppUpdateAvailable(OnInAppUpdateAvailable onInAppUpdateAvailable) {
            this.onInAppUpdateAvailable = onInAppUpdateAvailable;
            return this;
        }

        /**
         * Build the {@link QueenOfVersions} using parameters set in this builder.
         *
         * @param activity Activity instance used to host an update check
         * @return new instance of {@link QueenOfVersions}
         */
        public QueenOfVersions build(FragmentActivity activity) {
            return new QueenOfVersions(
                    storage != null ? storage : new QueenOfVersionsDefaultNamedPreferenceStorage(activity),
                    onPrinceOfVersionsSuccess != null ? onPrinceOfVersionsSuccess : new QueenOnPrinceOfVersionsSuccess(),
                    onPrinceOfVersionsError != null ? onPrinceOfVersionsError : new QueenOnPrinceOfVersionsError(),
                    onUpdateNotAllowed != null ? onUpdateNotAllowed : new OnUpdateNotAllowedReportNoUpdate(),
                    onInAppUpdateAvailable != null ? onInAppUpdateAvailable : new OnInAppUpdateAvailableResumeWithCurrentResolution(),
                    activity,
                    princeOfVersions != null ? princeOfVersions : new PrinceOfVersions(activity)
            );
        }
    }

    /**
     * Options used to start update check without {@link PrinceOfVersions} check.
     */
    public static class Options {

        @Nullable
        private Storage storage;

        @Nullable
        private UpdateStatus updateStatus;

        @Nullable
        private UpdateResult updateResult;

        private OnUpdateNotAllowed onUpdateNotAllowed;

        private OnInAppUpdateAvailable onInAppUpdateAvailable;

        private Options(
                @Nullable Storage storage,
                @Nullable UpdateStatus updateStatus,
                @Nullable UpdateResult updateResult,
                OnUpdateNotAllowed onUpdateNotAllowed,
                OnInAppUpdateAvailable onInAppUpdateAvailable
        ) {
            this.storage = storage;
            this.updateStatus = updateStatus;
            this.updateResult = updateResult;
            this.onUpdateNotAllowed = onUpdateNotAllowed;
            this.onInAppUpdateAvailable = onInAppUpdateAvailable;
        }

        /**
         * Builder used to build {@link Options}.
         * All configurable parameters are optional.
         */
        public static class Builder {

            @Nullable
            private Storage storage;

            @Nullable
            private UpdateStatus updateStatus;

            @Nullable
            private UpdateResult updateResult;

            @Nullable
            private OnUpdateNotAllowed onUpdateNotAllowed;

            @Nullable
            private OnInAppUpdateAvailable onInAppUpdateAvailable;

            /**
             * Add custom storage used for saving last notified update, for the sake of not notifying same update twice
             * in case of update with {@link co.infinum.princeofversions.NotificationType} ONCE.
             *
             * @param storage implementation of the storage.
             * @return this
             */
            public Options.Builder withStorage(Storage storage) {
                this.storage = storage;
                return this;
            }

            /**
             * Use {@link UpdateStatus} to specify which type of update to start.
             *
             * @param updateStatus status.
             * @return this
             */
            public Options.Builder withUpdateStatus(UpdateStatus updateStatus) {
                this.updateStatus = updateStatus;
                return this;
            }

            /**
             * Use to set information about update received from {@link PrinceOfVersions}.
             *
             * @param updateResult information about the update.
             * @return this
             */
            public Options.Builder withUpdateResult(UpdateResult updateResult) {
                this.updateResult = updateResult;
                return this;
            }

            /**
             * Use to set handler for not allowed update flow.
             *
             * @param onUpdateNotAllowedHandler implementation of handler
             * @return this
             * @see OnUpdateNotAllowed
             */
            public Options.Builder withOnUpdateNotAllowedHandler(OnUpdateNotAllowed onUpdateNotAllowedHandler) {
                this.onUpdateNotAllowed = onUpdateNotAllowedHandler;
                return this;
            }

            /**
             * Use to set handler for available In-App update.
             *
             * @param onInAppUpdateAvailable implementation of handler
             * @return this
             * @see OnInAppUpdateAvailable
             */
            public Options.Builder withOnInAppUpdateAvailable(OnInAppUpdateAvailable onInAppUpdateAvailable) {
                this.onInAppUpdateAvailable = onInAppUpdateAvailable;
                return this;
            }

            /**
             * Build the {@link Options} using parameters set in this builder.
             *
             * @return new instance of the {@link Options}
             */
            public Options build() {
                return new Options(
                        storage,
                        updateStatus,
                        updateResult,
                        onUpdateNotAllowed != null ? onUpdateNotAllowed : new OnUpdateNotAllowedReportNoUpdate(),
                        onInAppUpdateAvailable != null ? onInAppUpdateAvailable : new OnInAppUpdateAvailableResumeWithCurrentResolution()
                );
            }
        }
    }

    static class CallbackAdapter implements QueenOfVersions.Callback {

        static final Callback DEFAULT = new Callback() {
            @Override
            public void onCanceled() {
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
            public void onDownloaded(QueenOfVersions.UpdateHandler handler, QueenOfVersionsInAppUpdateInfo inAppUpdate) {
                handler.completeUpdate();
            }

            @Override
            public void onDownloading(QueenOfVersionsInAppUpdateInfo inAppUpdate, long bytesDownloadedSoFar, long totalBytesToDownload) {
                // no-op
            }

            @Override
            public void onInstalled(QueenOfVersionsInAppUpdateInfo appUpdateInfo) {
                // no-op
            }

            @Override
            public void onInstalling(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
                // no-op
            }

            @Override
            public void onMandatoryUpdateNotAvailable(
                    int mandatoryVersion,
                    QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                    Map<String, String> metadata,
                    UpdateInfo updateInfo
            ) {
                onNoUpdate(metadata, updateInfo);
            }

            @Override
            public void onPending(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
                // no-op
            }

            @Override
            public void onUpdateAccepted(
                    QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                    UpdateStatus updateStatus,
                    @Nullable UpdateResult updateResult
            ) {
                // no-op
            }

            @Override
            public void onUpdateDeclined(
                    QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                    UpdateStatus updateStatus,
                    @Nullable UpdateResult updateResult
            ) {
                // no-op
            }
        };

        private Callback fallback;

        @Nullable
        private OnDownloaded onDownloaded;

        @Nullable
        private OnCanceled onCanceled;

        @Nullable
        private OnInstalled onInstalled;

        @Nullable
        private OnPending onPending;

        @Nullable
        private OnError onError;

        @Nullable
        private OnNoUpdate onNoUpdate;

        @Nullable
        private OnDownloading onDownloading;

        @Nullable
        private OnInstalling onInstalling;

        @Nullable
        private OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable;

        @Nullable
        private OnUpdateAccepted onUpdateAccepted;

        @Nullable
        private OnUpdateDeclined onUpdateDeclined;

        CallbackAdapter(Callback fallback) {
            this.fallback = fallback;
        }

        CallbackAdapter() {
            this(DEFAULT);
        }

        void withFallback(Callback callback) {
            this.fallback = callback;
        }

        void withOnDownloaded(OnDownloaded action) {
            this.onDownloaded = action;
        }

        void withOnCanceled(OnCanceled onCanceled) {
            this.onCanceled = onCanceled;
        }

        void withOnInstalled(OnInstalled onInstalled) {
            this.onInstalled = onInstalled;
        }

        void withOnPending(OnPending onPending) {
            this.onPending = onPending;
        }

        void withOnError(OnError onError) {
            this.onError = onError;
        }

        void withOnNoUpdate(OnNoUpdate onNoUpdate) {
            this.onNoUpdate = onNoUpdate;
        }

        void withOnDownloading(OnDownloading onDownloading) {
            this.onDownloading = onDownloading;
        }

        void withOnInstalling(OnInstalling onInstalling) {
            this.onInstalling = onInstalling;
        }

        void withOnMandatoryUpdateNotAvailable(OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable) {
            this.onMandatoryUpdateNotAvailable = onMandatoryUpdateNotAvailable;
        }

        void withOnUpdateAccepted(OnUpdateAccepted onUpdateAccepted) {
            this.onUpdateAccepted = onUpdateAccepted;
        }

        void withOnUpdateDeclined(OnUpdateDeclined onUpdateDeclined) {
            this.onUpdateDeclined = onUpdateDeclined;
        }

        @Override
        public void onDownloaded(UpdateHandler handler, QueenOfVersionsInAppUpdateInfo inAppUpdate) {
            if (onDownloaded != null) {
                onDownloaded.onDownloaded(handler, inAppUpdate);
            } else {
                fallback.onDownloaded(handler, inAppUpdate);
            }
        }

        @Override
        public void onDownloading(QueenOfVersionsInAppUpdateInfo inAppUpdate, long bytesDownloadedSoFar, long totalBytesToDownload) {
            if (onDownloading != null) {
                onDownloading.onDownloading(inAppUpdate, bytesDownloadedSoFar, totalBytesToDownload);
            } else {
                fallback.onDownloading(inAppUpdate, bytesDownloadedSoFar, totalBytesToDownload);
            }
        }

        @Override
        public void onInstalled(QueenOfVersionsInAppUpdateInfo appUpdateInfo) {
            if (onInstalled != null) {
                onInstalled.onInstalled(appUpdateInfo);
            } else {
                fallback.onInstalled(appUpdateInfo);
            }
        }

        @Override
        public void onInstalling(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
            if (onInstalling != null) {
                onInstalling.onInstalling(inAppUpdateInfo);
            } else {
                fallback.onInstalling(inAppUpdateInfo);
            }
        }

        @Override
        public void onMandatoryUpdateNotAvailable(
                int mandatoryVersion,
                QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                Map<String, String> metadata,
                UpdateInfo updateInfo
        ) {
            if (onMandatoryUpdateNotAvailable != null) {
                onMandatoryUpdateNotAvailable.onMandatoryUpdateNotAvailable(mandatoryVersion, inAppUpdateInfo, metadata, updateInfo);
            } else {
                fallback.onMandatoryUpdateNotAvailable(mandatoryVersion, inAppUpdateInfo, metadata, updateInfo);
            }
        }

        @Override
        public void onPending(QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
            if (onPending != null) {
                onPending.onPending(inAppUpdateInfo);
            } else {
                fallback.onPending(inAppUpdateInfo);
            }
        }

        @Override
        public void onUpdateAccepted(
                QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                UpdateStatus updateStatus,
                @Nullable UpdateResult updateResult
        ) {
            if (onUpdateAccepted != null) {
                onUpdateAccepted.onUpdateAccepted(inAppUpdateInfo, updateStatus, updateResult);
            } else {
                fallback.onUpdateAccepted(inAppUpdateInfo, updateStatus, updateResult);
            }
        }

        @Override
        public void onUpdateDeclined(
                QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
                UpdateStatus updateStatus,
                @Nullable UpdateResult updateResult
        ) {
            if (onUpdateDeclined != null) {
                onUpdateDeclined.onUpdateDeclined(inAppUpdateInfo, updateStatus, updateResult);
            } else {
                fallback.onUpdateDeclined(inAppUpdateInfo, updateStatus, updateResult);
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
        public void onCanceled() {
            if (onCanceled != null) {
                onCanceled.onCanceled();
            } else {
                fallback.onCanceled();
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
            if (onUpdateAccepted != null) {
                copy.withOnUpdateAccepted(onUpdateAccepted);
            }
            if (onUpdateDeclined != null) {
                copy.withOnUpdateDeclined(onUpdateDeclined);
            }
            return copy;
        }
    }

    /**
     * Thrown in case {@link com.google.android.play.core.install.model.InstallStatus} reports UNKNOWN.
     */
    public static class UnknownVersionException extends Exception {

    }
}
