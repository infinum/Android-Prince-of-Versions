package co.infinum.queenofversions;

import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import co.infinum.queenofversions.mocks.MockInstallState;

import static co.infinum.queenofversions.InAppUpdateError.API_NOT_AVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.DOWNLOAD_NOT_PRESENT;
import static co.infinum.queenofversions.InAppUpdateError.ERROR_UNKNOWN;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_NOT_ALLOWED;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_UNAVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.INTERNAL_ERROR;
import static co.infinum.queenofversions.InAppUpdateError.INVALID_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueenOfVersionsTest {

    @Mock
    GoogleInAppUpdateCallback googleInAppUpdateCallback;

    @Mock
    GoogleAppUpdater googleAppUpdater;

    @Mock
    InAppUpdateProcessCallback updaterStateCallback;

    private static Throwable anyThrowable() {
        return any(Throwable.class);
    }

    private static Map<String, String> anyMap() {
        return ArgumentMatchers.anyMap();
    }

    /**
     * Testing GoogleInAppUpdateCallback
     */

    @Test
    public void testGoogleCompleteUpdateCall() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.completeUpdate();

        verify(googleAppUpdater, times(1)).completeUpdate();
    }

    @Test
    public void testStartingGoogleInAppUpdaterWhenThereIsNewUpdate() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.onNewUpdate("2.0.0", true, Collections.<String, String>emptyMap());

        verify(googleAppUpdater, times(1)).initGoogleUpdate(anyBoolean(), nullable(String.class));
        verify(googleAppUpdater, times(0)).completeUpdate();
    }

    /**
     * If Prince returns that there is no updates, we still have to check with Google but offer it as non mandatory update?
     */

    @Test
    public void testStartingGoogleInAppUpdaterIfThereIsNoNewUpdate() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.onNoUpdate(Collections.<String, String>emptyMap());

        verify(googleAppUpdater, times(1)).initGoogleUpdate(false, null);
        verify(googleAppUpdater, times(0)).completeUpdate();
    }

    @Test
    public void testGettingErrorOnPrinceCallback() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.onError(new Throwable("Error"));

        verify(updaterStateCallback, times(1)).onFailed(any(GoogleInAppUpdateException.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusDownload() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.DOWNLOADED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testStatusChangedOnInstallStatusCanceled() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.CANCELED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(1)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testStatusChangedOnInstallStatusInstalling() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.INSTALLING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(1)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }


    @Test
    public void testStatusChangedOnInstallStatusInstalled() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.INSTALLED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(1)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testStatusChangedOnInstallStatusPending() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.PENDING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(1)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testStatusChangedOnInstallStatusUnknown() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.UNKNOWN, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(1)).onUnknown();
    }

    @Test
    public void testStatusChangedOnInstallStatusDownloading() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.DOWNLOADING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onRequiresUI();
        verify(updaterStateCallback, times(1)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testStatusChangedInstallStatusRequiresUI() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.REQUIRES_UI_INTENT, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0)).onDownloaded(any(GoogleInAppUpdateFlexibleHandler.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate();
        verify(updaterStateCallback, times(0)).onFailed(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(1)).onRequiresUI();
        verify(updaterStateCallback, times(0)).onDownloading();
        verify(updaterStateCallback, times(0)).onInstalling();
        verify(updaterStateCallback, times(0)).onInstalled();
        verify(updaterStateCallback, times(0)).onPending();
        verify(updaterStateCallback, times(0)).onUnknown();
    }

    @Test
    public void testErrorCodeOnApiNotAvailableError() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_API_NOT_AVAILABLE);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(API_NOT_AVAILABLE.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnDownloadNotPresent() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(DOWNLOAD_NOT_PRESENT.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallNotAllowed() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(INSTALL_NOT_ALLOWED.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallUnavailable() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_UNAVAILABLE);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(INSTALL_UNAVAILABLE.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnInternalError() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INTERNAL_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(INTERNAL_ERROR.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnInvalidRequest() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INVALID_REQUEST);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(INVALID_REQUEST.name());
            }
        }));
    }

    @Test
    public void testErrorCodeOnUnknownError() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_UNKNOWN);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getMessage().equals(ERROR_UNKNOWN.name());
            }
        }));
    }

    /**
     * Testing all possible Prince Of Versions and Google InApp Updates version permutations
     */

    @Test
    public void testPrinceAndGoogleSameVersionMandatory() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 11, true);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE);
    }

    @Test
    public void testPrinceAndGoogleSameVersionOptional() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 11, false);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleMandatory() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 12, true);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleOptional() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 12, false);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testPrinceHigherVersionThanGoogleMandatory() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "12", 11, true);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testPrinceHigherVersionThanGoogleOptional() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 12, false);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleAndAppMandatory() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "10", 12, true);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleAndAppOptional() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "11", 12, false);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    @Test
    public void testAppHigherVersionThanGoogleButLowerThanPrinceMandatory() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "12", 10, true);

        verify(googleAppUpdater, times(0)).startUpdate(anyInt());
    }

    @Test
    public void testAppHigherVersionThanGoogleButLowerThanPrinceOptional() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, "12", 10, false);

        verify(googleAppUpdater, times(0)).startUpdate(anyInt());
    }

    @Test
    public void testNoUpdateFromGoogle() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_NOT_AVAILABLE, "12", 10, true);

        verify(googleAppUpdater, times(1)).wrongVersion();
    }

    @Test
    public void testInvalidVersionOnPrinceOfVersions() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_AVAILABLE, null, 10, true);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE);
    }

    /**
     * Testing GoogleInAppUpdateFailure listener
     */

    @Test
    public void testOnFailureCallInAppUpdateFailureListener() {
        GoogleInAppUpdateFailureListener googleInAppUpdateFailureListener = new GoogleInAppUpdateFailureListener(updaterStateCallback);
        googleInAppUpdateFailureListener.onFailure(new GoogleInAppUpdateException(new Throwable("Error")));

        verify(updaterStateCallback, times(1)).onFailed(any(GoogleInAppUpdateException.class));
    }

    /**
     * Testing all possible on resume google calls
     */

    @Test
    public void testOnResumeHasBeenCalledForImmediate() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS, InstallStatus.CANCELED,
            false);

        verify(googleAppUpdater, times(0)).notifyUser();
        verify(googleAppUpdater, times(1)).restartUpdate();
    }

    @Test
    public void testOnResumeHasBeenCallForFlexible() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.UNKNOWN, InstallStatus.DOWNLOADED, true);

        verify(googleAppUpdater, times(1)).notifyUser();
        verify(googleAppUpdater, times(0)).restartUpdate();
    }

    @Test
    public void testUnsupportedVersionOnPrince() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_NOT_AVAILABLE, "12", 11, true);

        verify(googleAppUpdater,times(1)).wrongVersion();
    }

    @Test
    public void testNoUpdateFromGoogleOrPrince(){
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleSuccess(UpdateAvailability.UPDATE_NOT_AVAILABLE, "11", 11, true);

        verify(googleAppUpdater,times(1)).noUpdate();
    }
}
