package co.infinum.queenofversions;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.jetbrains.annotations.NotNull;

import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.PrinceOfVersionsCancelable;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.princeofversions.UpdaterCallback;

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
 * {@link QueenOfVersionsCallback}
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
 *          {@link QueenOfVersionCallbackUpdate} googleCallback = new {@link QueenOfVersionCallbackUpdate}(requestCode,activity,listener,
 *          appVersionCode);
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
public class QueenOfVersionCallbackUpdate implements UpdaterCallback, InstallStateUpdatedListener, QueenOfVersionFlexibleUpdateHandler {

    private UpdateStateDelegate flexibleStateListener;
    private GoogleAppUpdater googleAppUpdater;
    private final int appVersionCode;

    /**
     * Creates {@link QueenOfVersionCallbackUpdate} using provided {@link Activity}, {@link QueenOfVersionsCallback} and two integers
     * that represent requestCode and appVersionCode.
     *
     * @param requestCode    integer that can be used for {@link Intent} identification in onActivityResult method
     * @param activity       activity is used for purposes of Google's in-app updates methods that are used in this library
     * @param listener       listener is used as callback for notifying update statuses during update
     * @param appVersionCode integer that represents version of an application that's currently running
     */
    public QueenOfVersionCallbackUpdate(int requestCode, Activity activity, QueenOfVersionsCallback listener, int appVersionCode) {
        this.flexibleStateListener = new UpdateStateDelegate(false, listener);
        this.appVersionCode = appVersionCode;
        this.googleAppUpdater = new QueenOfVersionsAppUpdater(activity, AppUpdateManagerFactory.create(activity), requestCode,
            flexibleStateListener,
            this);
    }

    @VisibleForTesting
    QueenOfVersionCallbackUpdate(int requestCode, GoogleAppUpdater appUpdater, QueenOfVersionsCallback flexibleStateListener,
        int appVersionCode) {
        this.flexibleStateListener = new UpdateStateDelegate(false, flexibleStateListener);
        this.appVersionCode = appVersionCode;
        this.googleAppUpdater = appUpdater;
    }

    /**
     * Method is called if {@link PrinceOfVersions} successfully finishes update check. Whatever the outcome is, we are always
     * checking with Google play for new update.However, the reason why are we still using {@link PrinceOfVersions} for update
     * determination is because of the {@param isMandatory} which helps us to determine if we want to invoke IMMEDIATE or FLEXIBLE update
     * on Google Play store.
     */
    @Override
    public void onSuccess(UpdateResult result) {
        if (result.getStatus() == UpdateStatus.REQUIRED_UPDATE_NEEDED) {
            checkWithGoogleForAnUpdate(true, String.valueOf(result.getInfo().getLastVersionAvailable()));
        } else {
            checkWithGoogleForAnUpdate(false, String.valueOf(result.getInfo().getLastVersionAvailable()));
        }
    }

    /**
     * Method is called if was some error on {@link PrinceOfVersions} while checking for an update.
     *
     * @param error Throwable that describes error occurred.
     */
    @Override
    public void onError(@NotNull Throwable error) {
        flexibleStateListener.onFailed(new GoogleInAppUpdateException(error));
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
            flexibleStateListener.onRequiresUI();
        } else if (installState.installStatus() == InstallStatus.INSTALLED) {
            flexibleStateListener.onInstalled();
        } else if (installState.installStatus() == InstallStatus.PENDING) {
            flexibleStateListener.onPending();
        } else if (installState.installStatus() == InstallStatus.UNKNOWN) {
            flexibleStateListener.onUnknown();
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
    void handleSuccess(@UpdateAvailability int updateAvailability, String princeVersionCode, int googleUpdateVersionCode,
        boolean isMandatory) {
        VersionCode versionCode = checkVersionCode(princeVersionCode, googleUpdateVersionCode, isMandatory);
        if (updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
            if (versionCode == VersionCode.FLEXIBLE) {
                googleAppUpdater.startUpdate(AppUpdateType.FLEXIBLE);
            } else if (versionCode == VersionCode.IMMEDIATE) {
                googleAppUpdater.startUpdate(AppUpdateType.IMMEDIATE);
            } else if (versionCode == VersionCode.FLEXIBLE_NOT_AVAILABLE) {
                googleAppUpdater.mandatoryUpdateNotAvailable();
            }
        } else {
            googleAppUpdater.noUpdate();
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
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(API_NOT_AVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(DOWNLOAD_NOT_PRESENT));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(INSTALL_NOT_ALLOWED));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INSTALL_UNAVAILABLE) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(INSTALL_UNAVAILABLE));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INTERNAL_ERROR) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(INTERNAL_ERROR));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_INVALID_REQUEST) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(INVALID_REQUEST));
        } else if (installState.installErrorCode() == InstallErrorCode.ERROR_UNKNOWN) {
            flexibleStateListener.onFailed(new GoogleInAppUpdateException(ERROR_UNKNOWN));
        }
    }

    /**
     * Method that is called every time we check for an update. Whether there is or isn't an update on {@link PrinceOfVersions} we are
     * still going to check with Google to be sure.
     *
     * @param isMandatory       Determines if the update we are checking for is REQUIRED_UPDATE_NEEDED or NEW_UPDATE_AVAILABLE
     * @param princeVersionCode Version code that we got from {@link PrinceOfVersions} after parsing JSON file
     */
    private void checkWithGoogleForAnUpdate(boolean isMandatory, String princeVersionCode) {
        googleAppUpdater.initGoogleUpdate(isMandatory, princeVersionCode);
    }

    /**
     * Method that is used to check what kind of update do we want to invoke on Google Play Store. Depending on version codes
     * on JSON file, application  and Google Play Store, we can invoke FLEXIBLE or IMMEDIATE update on Google Play Store.
     *
     * @param princeVersionCode Version code that we got from {@link PrinceOfVersions} after parsing JSON file
     * @param googleVersionCode Version code that we got from Google Play Store
     * @param isMandatory       Determines whether the update we have is REQUIRED_UPDATE_NEEDED or NEW_UPDATE_AVAILABLE
     * @return Returns an {@link AppUpdateType} depending on version codes we have
     */
    private VersionCode checkVersionCode(String princeVersionCode, int googleVersionCode, boolean isMandatory) {
        int princeOfVersionsCode;
        if (princeVersionCode == null) {
            return VersionCode.FLEXIBLE;
        } else {
            princeOfVersionsCode = Integer.parseInt(princeVersionCode);
        }

        if (princeOfVersionsCode <= googleVersionCode && isMandatory) {
            if (appVersionCode > princeOfVersionsCode) {
                return VersionCode.FLEXIBLE;
            } else {
                return VersionCode.IMMEDIATE;
            }
        } else if (princeOfVersionsCode > googleVersionCode) {
            if (appVersionCode < googleVersionCode && isMandatory) {
                return VersionCode.FLEXIBLE_NOT_AVAILABLE;
            } else {
                return VersionCode.FLEXIBLE;
            }
        } else {
            return VersionCode.FLEXIBLE;
        }
    }

    /**
     * Method that is called when you want to cancel listener on your Google's in-app update.
     * By calling this method we are stopping responses from {@link InstallStateUpdatedListener}.
     */
    public void cancel() {
        flexibleStateListener.cancel();
    }

    enum VersionCode {
        FLEXIBLE,
        IMMEDIATE,
        FLEXIBLE_NOT_AVAILABLE
    }
}
