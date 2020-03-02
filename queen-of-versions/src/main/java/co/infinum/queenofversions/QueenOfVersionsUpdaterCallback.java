package co.infinum.queenofversions;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.VisibleForTesting;
import co.infinum.princeofversions.NotificationType;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.PrinceOfVersionsCancelable;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.princeofversions.UpdaterCallback;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static co.infinum.queenofversions.InAppUpdateError.API_NOT_AVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.DOWNLOAD_NOT_PRESENT;
import static co.infinum.queenofversions.InAppUpdateError.ERROR_UNKNOWN;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_NOT_ALLOWED;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_UNAVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.INTERNAL_ERROR;
import static co.infinum.queenofversions.InAppUpdateError.INVALID_REQUEST;

/**
 * This class represents the core component of Queen of Versions module.
 * <p>
 * The way to create instance of this class is by constructor with {@link Integer}, {@link Activity} and
 * {@link QueenOfVersionsUpdaterCallback}
 * arguments.
 * </p>
 * <p>
 * This callback is an upgrade to {@link UpdaterCallback}. The upgrade is integration of
 * {@link AppUpdateManager} to {@link PrinceOfVersions}. In this upgrade we are using Google's new way of handling updates from Google
 * Play Store and still keeping {@link PrinceOfVersions} for better update management. The problem with new Google's way is that we are
 * not in
 * position to determine if the update on Google Play Store is mandatory or optional. That's why for determination of the nature of the
 * update we are using {@link PrinceOfVersions}.
 * </p>
 * <p>
 * This class is used in the same manner as {@link UpdaterCallback}, by passing it to {@link PrinceOfVersions}. After the result is
 * computed, it is provided through one of UpdaterCallback's overridden methods. The difference is that now after every checkUpdates we
 * are also checking with Google's {@link AppUpdateManager}, but using the app update type (mandatory or optional)
 * from {@link PrinceOfVersions}.
 * </p>
 * Here is a most common usage of this callback:
 * <pre>
 *          {@link PrinceOfVersions} princeOfVersion = new {@link PrinceOfVersions}(context);
 *          {@link QueenOfVersionsUpdaterCallback} googleCallback
 *              = new {@link QueenOfVersionsUpdaterCallback}(requestCode, activity, listener, appVersionCode);
 *
 *          {@link PrinceOfVersionsCancelable} cancelable = princeOfVersion.checkForUpdates("http://example.com/some/update.json",
 *          googleCallback); // start checking for an update...
 * </pre>
 * <p>
 * IMPORTANT DISTINCTIONS: Throughout this module we are using definitions of app update types interchangeably, because the same definition
 * of update type is named differently on JSON and on Google Play Store, hence this warning.
 * <p>
 * For example:
 * REQUIRED_UPDATE_NEEDED update == IMMEDIATE update
 * FLEXIBLE update == NEW_UPDATE_AVAILABLE update
 */
class QueenOfVersionsUpdaterCallback implements UpdaterCallback, InstallStateUpdatedListener, QueenOfVersions.UpdateHandler {

    private final int appVersionCode;

    private final QueenOfVersionsCancelableCallback flexibleStateListener;

    private final GoogleAppUpdater googleAppUpdater;

    private final OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess;

    private final OnPrinceOfVersionsError onPrinceOfVersionsError;

    private final Storage storage;

    /**
     * Creates {@link QueenOfVersionsUpdaterCallback} using provided {@link Activity}, {@link QueenOfVersions.Callback} and two integers
     * that represent requestCode and appVersionCode.
     *
     * @param requestCode integer that can be used for {@link Intent} identification in onActivityResult method
     * @param activity    activity is used for purposes of Google's in-app updates methods that are used in this library
     * @param listener    listener is used as callback for notifying update statuses during update
     */
    QueenOfVersionsUpdaterCallback(
            int requestCode,
            Activity activity,
            QueenOfVersions.Callback listener,
            OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess,
            OnPrinceOfVersionsError onPrinceOfVersionsError,
            Storage storage
    ) {
        this.flexibleStateListener = new QueenOfVersionsCancelableCallback(false, listener);
        this.googleAppUpdater = new QueenOfVersionsAppUpdater(activity, AppUpdateManagerFactory.create(activity), requestCode,
                flexibleStateListener,
                this);
        try {
            this.appVersionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Could not find package name", e);
        }
        this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
        this.onPrinceOfVersionsError = onPrinceOfVersionsError;
        this.storage = storage;
    }

    @VisibleForTesting
    QueenOfVersionsUpdaterCallback(
            int requestCode,
            GoogleAppUpdater appUpdater,
            QueenOfVersions.Callback flexibleStateListener,
            int appVersionCode,
            OnPrinceOfVersionsSuccess onPrinceOfVersionsSuccess,
            OnPrinceOfVersionsError onPrinceOfVersionsError,
            Storage storage
    ) {
        this.flexibleStateListener = new QueenOfVersionsCancelableCallback(false, flexibleStateListener);
        this.appVersionCode = appVersionCode;
        this.googleAppUpdater = appUpdater;
        this.onPrinceOfVersionsSuccess = onPrinceOfVersionsSuccess;
        this.onPrinceOfVersionsError = onPrinceOfVersionsError;
        this.storage = storage;
    }

    @VisibleForTesting
    QueenOfVersionsUpdaterCallback(
            int requestCode,
            GoogleAppUpdater appUpdater,
            QueenOfVersions.Callback flexibleStateListener,
            int appVersionCode,
            Storage storage
    ) {
        this(
                requestCode,
                appUpdater,
                flexibleStateListener,
                appVersionCode,
                new QueenOfVersions.QueenOnPrinceOfVersionsSuccess(),
                new QueenOfVersions.QueenOnPrinceOfVersionsError(),
                storage
        );
    }

    /**
     * OnSuccess method is called when {@link PrinceOfVersions} successfully finishes update check. There are two successful outcomes.
     * Firstly, the could be a new update and that new update can be REQUIRED_NEW_UPDATE that corresponds to IMMEDIATE, or it could be
     * NEW_UPDATE_AVAILABLE that corresponds to FLEXIBLE update. Secondly, there could be no update on {@link PrinceOfVersions}. However,
     * no matter what is the outcome, whether there is or there is not an update, we are still going to check with the Google's Play
     * store if there is an update. The only change is that for a REQUIRED_NEW_UPDATE flag, we are invoking IMMEDIATE update on Google,
     * but for everything else we are invoking FLEXIBLE update.
     */

    @Override
    public void onSuccess(@Nonnull UpdateResult result) {
        try {
            UpdateStatus status = onPrinceOfVersionsSuccess.handleUpdateResultAsStatus(result);
            continueUpdateCheckBasedOnStatus(status, result);
        } catch (Throwable error) {
            flexibleStateListener.onError(error);
        }
    }

    /**
     * Method is called if was some error on {@link PrinceOfVersions} while checking for an update.
     *
     * @param error Throwable that describes error occurred.
     */
    @Override
    public void onError(@Nonnull Throwable error) {
        try {
            UpdateStatus status = onPrinceOfVersionsError.continueUpdateCheckAsStatus(error);
            continueUpdateCheckBasedOnStatus(status, null);
        } catch (Throwable rethrown) {
            flexibleStateListener.onError(rethrown);
        }
    }

    void continueUpdateCheckBasedOnStatus(UpdateStatus status, @Nullable UpdateResult result) {
        switch (status) {
            case NEW_UPDATE_AVAILABLE:
                checkWithGoogleForAnUpdate(
                        false,
                        result != null ? result.getUpdateVersion() : null,
                        result
                );
                break;
            case REQUIRED_UPDATE_NEEDED:
                checkWithGoogleForAnUpdate(
                        true,
                        result != null ? result.getUpdateVersion() : null,
                        result
                );
                break;
            default:
                flexibleStateListener.onNoUpdate(
                        result != null ? result.getMetadata() : null,
                        result != null ? result.getInfo() : null
                );
        }
    }

    /**
     * Method that is called during Google's FLEXIBLE in-app update.
     *
     * @param installState Google's object with which we can determine in which stage of update is our update
     */
    @Override
    public void onStateUpdate(InstallState installState) {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleStateListener.onDownloaded(this);
        } else if (installState.installStatus() == InstallStatus.CANCELED) {
            flexibleStateListener.onCanceled();
        } else if (installState.installStatus() == InstallStatus.INSTALLING) {
            flexibleStateListener.onInstalling();
        } else if (installState.installStatus() == InstallStatus.DOWNLOADING) {
            flexibleStateListener.onDownloading();
        } else if (installState.installStatus() == InstallStatus.REQUIRES_UI_INTENT) {
            // shouldn't happen, let's ignore it for now
        } else if (installState.installStatus() == InstallStatus.INSTALLED) {
            flexibleStateListener.onInstalled();
        } else if (installState.installStatus() == InstallStatus.PENDING) {
            flexibleStateListener.onPending();
        } else if (installState.installStatus() == InstallStatus.UNKNOWN) {
            flexibleStateListener.onError(new QueenOfVersions.UnknownVersionException());
        } else if (installState.installStatus() == InstallStatus.FAILED) {
            checkErrorStates(installState);
        }
    }

    @Override
    public void completeUpdate() {
        googleAppUpdater.completeUpdate();
    }

    /**
     * Method is called if we have successful update check on Google Play Store.
     *
     * @param updateAvailability      Determines the availability of update we have on Google Play Store
     * @param princeVersionCode       Version code of the application we have have on our JSON that {@link PrinceOfVersions} parses
     * @param googleUpdateVersionCode Version code of the application we have on Google Play Store
     * @param isMandatory             Determines if the update we have on our JSON file is REQUIRED_UPDATE_NEEDED or NEW_UPDATE_AVAILABLE
     */
    void handleSuccess(@UpdateAvailability int updateAvailability, @Nullable Integer princeVersionCode, int googleUpdateVersionCode,
            boolean isMandatory, @Nullable UpdateResult updateResult) {

        if (flexibleStateListener.isCanceled()) {
            return;
        }
        UpdateInfo updateInfo = updateResult != null ? updateResult.getInfo() : null;

        if (updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
            UpdateResolution updateResolution = checkUpdateResolution(princeVersionCode, googleUpdateVersionCode, isMandatory, updateInfo);
            updateResolution = relaxResolution(
                    updateResolution,
                    // default to once if there is no update info because of any error
                    updateInfo != null ? updateInfo.getNotificationFrequency() : NotificationType.ONCE,
                    googleUpdateVersionCode
            );
            if (updateResolution == UpdateResolution.FLEXIBLE) {
                googleAppUpdater.startUpdate(AppUpdateType.FLEXIBLE);
            } else if (updateResolution == UpdateResolution.IMMEDIATE) {
                googleAppUpdater.startUpdate(AppUpdateType.IMMEDIATE);
            } else if (updateResolution == UpdateResolution.IMMEDIATE_NOT_AVAILABLE) {
                Integer requiredVersion = updateInfo != null ? updateInfo.getRequiredVersion() : null;
                if (requiredVersion != null) {
                    googleAppUpdater.mandatoryUpdateNotAvailable(
                            requiredVersion,
                            googleUpdateVersionCode,
                            updateResult.getMetadata(),
                            updateInfo
                    );
                } else {
                    googleAppUpdater.noUpdate(
                            updateResult != null ? updateResult.getMetadata() : null,
                            updateInfo
                    );
                }
            } else {
                // Shouldn't happen if we cover all cases of @VersionCode
                googleAppUpdater.noUpdate(
                        updateResult != null ? updateResult.getMetadata() : null,
                        updateInfo
                );
            }
        } else {
            googleAppUpdater.noUpdate(
                    updateResult != null ? updateResult.getMetadata() : null,
                    updateInfo
            );
        }
    }

    //This method is called when you leave app during an immediate update, but also it checks if user has left app during flexible update
    //In case of flexible update we notify user about downloaded update so he can do install it or whatever

    //TODO check this because I'm pretty sure this won't even be called in case of FLEXIBLE update because we are not registering this
    // flow for FLEXIBLE update!

    /**
     * Method is called when user leaves the application during Google's IMMEDIATE update. When user leaves during IMMEDIATE update we
     * want to continue our update process.
     *
     * @param updateAvailability Determines the availability of update we have on Google Play Store
     * @param installStatus      Google's object with which we can determine in which stage of update is our update
     * @param isFlexible         don't know
     */
    void handleResumeSuccess(@UpdateAvailability int updateAvailability, @InstallStatus int installStatus, boolean isFlexible) {
        if (updateAvailability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            googleAppUpdater.restartUpdate();
        } else if (installStatus == InstallStatus.DOWNLOADED && isFlexible) {
            googleAppUpdater.notifyUser();
        }
    }

    /**
     * Method that is called when update has failed.
     *
     * @param installState Google's object with which we can determine in which stage of update is our update
     */
    private void checkErrorStates(InstallState installState) {
        if (installState.installErrorCode() == InstallErrorCode.ERROR_API_NOT_AVAILABLE) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(API_NOT_AVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(DOWNLOAD_NOT_PRESENT));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(INSTALL_NOT_ALLOWED));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_UNAVAILABLE) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(INSTALL_UNAVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INTERNAL_ERROR) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(INTERNAL_ERROR));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INVALID_REQUEST) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(INVALID_REQUEST));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_UNKNOWN) {
            flexibleStateListener.onError(new GoogleInAppUpdateException(ERROR_UNKNOWN));
        }
    }

    /**
     * Method that is called every time we check for an update. Whether there is or isn't an update on {@link PrinceOfVersions} we are
     * still going to check with Google to be sure.
     *
     * @param isMandatory       Determines if the update we are checking for is REQUIRED_UPDATE_NEEDED or NEW_UPDATE_AVAILABLE
     * @param princeVersionCode Version code that we got from {@link PrinceOfVersions} after parsing JSON file
     * @param updateResult        All information about the update that {@link PrinceOfVersions} got from parsing JSON file
     */
    private void checkWithGoogleForAnUpdate(boolean isMandatory, @Nullable Integer princeVersionCode, @Nullable UpdateResult updateResult) {
        googleAppUpdater.initGoogleUpdate(isMandatory, princeVersionCode, updateResult);
    }

    /**
     * Method that is used to check what kind of update do we want to invoke on Google Play Store. Depending on version codes
     * on JSON file, application  and Google Play Store, we can invoke FLEXIBLE or IMMEDIATE update on Google Play Store.
     *
     * @param princeOfVersionsCode Version code that we got from {@link PrinceOfVersions} after parsing JSON file
     * @param googleVersionCode    Version code that we got from Google Play Store
     * @param isMandatory          Determines whether the update we have is REQUIRED_UPDATE_NEEDED or NEW_UPDATE_AVAILABLE
     * @return Returns an {@link AppUpdateType} depending on version codes we have
     */
    private UpdateResolution checkUpdateResolution(
            @Nullable Integer princeOfVersionsCode,
            int googleVersionCode,
            boolean isMandatory,
            @Nullable UpdateInfo updateInfo
    ) {
        if (princeOfVersionsCode == null) {
            if (isMandatory) {
                return UpdateResolution.IMMEDIATE;
            } else {
                return UpdateResolution.FLEXIBLE;
            }
        }

        if (princeOfVersionsCode <= googleVersionCode && isMandatory) {
            return UpdateResolution.IMMEDIATE;
        } else if (princeOfVersionsCode > googleVersionCode) {
            if (appVersionCode < googleVersionCode && isMandatory) {
                if (updateInfo != null && updateInfo.getRequiredVersion() != null) {
                    if (updateInfo.getRequiredVersion() <= googleVersionCode) {
                        return UpdateResolution.IMMEDIATE;
                    } else {
                        return UpdateResolution.IMMEDIATE_NOT_AVAILABLE;
                    }
                } else {
                    return UpdateResolution.IMMEDIATE_NOT_AVAILABLE;
                }
            } else {
                return UpdateResolution.FLEXIBLE;
            }
        } else {
            return UpdateResolution.FLEXIBLE;
        }
    }

    private UpdateResolution relaxResolution(UpdateResolution current, NotificationType notificationFrequency, int googleVersionCode) {
        switch (current) {
            case IMMEDIATE:
            case IMMEDIATE_NOT_AVAILABLE:
                return current;
            case FLEXIBLE:
                if (notificationFrequency == NotificationType.ONCE) {
                    Integer lastNotifiedVersion = storage.lastNotifiedVersion(null);
                    if (lastNotifiedVersion != null && lastNotifiedVersion.equals(googleVersionCode)) {
                        return UpdateResolution.SKIP;
                    } else {
                        return UpdateResolution.FLEXIBLE;
                    }
                } else {
                    return UpdateResolution.FLEXIBLE;
                }
            case SKIP:
            default:
                return UpdateResolution.SKIP;
        }
    }

    /**
     * Method that is called when you want to cancel listener on your Google's in-app update.
     * By calling this method we are stopping responses from {@link InstallStateUpdatedListener}.
     */
    public void cancel() {
        flexibleStateListener.cancel();
        googleAppUpdater.cancel();
    }

    enum UpdateResolution {
        FLEXIBLE,
        IMMEDIATE,
        IMMEDIATE_NOT_AVAILABLE,
        SKIP
    }
}
