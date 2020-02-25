package co.infinum.queenofversions;

import android.app.Activity;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.princeofversions.UpdaterCallback;
import javax.annotation.Nullable;

public class QueenOfVersions {

    private static final int DEFAULT_REQUEST_CODE = 128;

    private final QueenOfVersionsUpdaterCallback callback;

    QueenOfVersions(QueenOfVersionsUpdaterCallback callback) {
        this.callback = callback;
    }

    public UpdaterCallback getPrinceOfVersionsCallback() {
        return callback;
    }

    public interface Callback {

        Callback DEFAULT = new Callback() {
            @Override
            public void onDownloaded(QueenOfVersionsFlexibleUpdateHandler handler) {
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
            public void onUnknown() {
                // no-op
            }

            @Override
            public void onError(Throwable throwable) {
                // no-op
            }

            @Override
            public void onNoUpdate() {
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
            public void onRequiresUI() {
                // no-op
            }

            @Override
            public void onMandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion) {
                // no-op
            }
        };

        void onDownloaded(QueenOfVersionsFlexibleUpdateHandler handler);

        void onCanceled();

        void onInstalled();

        void onPending();

        void onUnknown();

        void onError(Throwable throwable);

        void onNoUpdate();

        void onDownloading();

        void onInstalling();

        void onRequiresUI();

        void onMandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion);
    }

    interface OnDownloaded {

        void onAction(QueenOfVersionsFlexibleUpdateHandler handler);
    }

    interface OnError {

        void onError(Throwable error);
    }

    interface OnMandatoryUpdateNotAvailable {

        void onAction(int mandatoryVersion, int availableVersion);
    }

    static class QueenOnPrinceOfVersionsSuccess implements OnPrinceOfVersionsSuccess {

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

        @Override
        public UpdateStatus continueUpdateCheckAsStatus(Throwable error) {
            return UpdateStatus.NEW_UPDATE_AVAILABLE;
        }
    }

    public static class Builder {

        private CallbackAdapter adapter = new CallbackAdapter();

        private int requestCode = DEFAULT_REQUEST_CODE;

        @Nullable
        private Storage storage = null;

        @Nullable
        private OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

        @Nullable
        private OnPrinceOfVersionsError onPrinceOfVersionsError;

        public Builder withCallback(QueenOfVersions.Callback callback) {
            adapter.withFallback(callback);
            return this;
        }

        public Builder withOnDownloaded(OnDownloaded action) {
            adapter.withOnDownloaded(action);
            return this;
        }

        public Builder withOnCanceled(Runnable onCanceled) {
            adapter.withOnCanceled(onCanceled);
            return this;
        }

        public Builder withOnInstalled(Runnable onInstalled) {
            adapter.withOnInstalled(onInstalled);
            return this;
        }

        public Builder withOnPending(Runnable onPending) {
            adapter.withOnPending(onPending);
            return this;
        }

        public Builder withOnError(OnError onError) {
            adapter.withOnError(onError);
            return this;
        }

        public Builder withOnUnknownError(Runnable onUnknownError) {
            adapter.withOnUnknownError(onUnknownError);
            return this;
        }

        public Builder withOnNoUpdate(Runnable onNoUpdate) {
            adapter.withOnNoUpdate(onNoUpdate);
            return this;
        }

        public Builder withOnDownloading(Runnable onDownloading) {
            adapter.withOnDownloading(onDownloading);
            return this;
        }

        public Builder withOnInstalling(Runnable onInstalling) {
            adapter.withOnInstalling(onInstalling);
            return this;
        }

        public Builder withOnMandatoryUpdateNotAvailable(OnMandatoryUpdateNotAvailable onMandatoryUpdateNotAvailable) {
            adapter.withOnMandatoryUpdateNotAvailable(onMandatoryUpdateNotAvailable);
            return this;
        }

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

        public QueenOfVersions build(Activity activity) {
            return new QueenOfVersions(new QueenOfVersionsUpdaterCallback(
                    requestCode,
                    activity,
                    adapter,
                    onPrinceOfVersionsSuccess != null ? onPrinceOfVersionsSuccess : new QueenOnPrinceOfVersionsSuccess(),
                    onPrinceOfVersionsError != null ? onPrinceOfVersionsError : new QueenOnPrinceOfVersionsError(),
                    storage != null ? storage : new QueenOfVersionsDefaultNamedPreferenceStorage(activity)
            ));
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
        private Runnable onUnknownError;

        @Nullable
        private OnError onError;

        @Nullable
        private Runnable onNoUpdate;

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

        void withOnUnknownError(Runnable onUnknownError) {
            this.onUnknownError = onUnknownError;
        }

        void withOnNoUpdate(Runnable onNoUpdate) {
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
        public void onDownloaded(QueenOfVersionsFlexibleUpdateHandler handler) {
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
        public void onUnknown() {
            if (onUnknownError != null) {
                onUnknownError.run();
            } else {
                fallback.onUnknown();
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
        public void onNoUpdate() {
            if (onNoUpdate != null) {
                onNoUpdate.run();
            } else {
                fallback.onNoUpdate();
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
        public void onRequiresUI() {
            // no-op
        }

        @Override
        public void onMandatoryUpdateNotAvailable(int mandatoryVersion, int availableVersion) {
            if (onMandatoryUpdateNotAvailable != null) {
                onMandatoryUpdateNotAvailable.onAction(mandatoryVersion, availableVersion);
            } else {
                fallback.onMandatoryUpdateNotAvailable(mandatoryVersion, availableVersion);
            }
        }
    }
}
