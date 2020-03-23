package co.infinum.queenofversions;

import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.queenofversions.mocks.MockAppUpdateData;
import co.infinum.queenofversions.mocks.MockInstallState;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static co.infinum.queenofversions.InAppUpdateError.API_NOT_AVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.DOWNLOAD_NOT_PRESENT;
import static co.infinum.queenofversions.InAppUpdateError.ERROR_UNKNOWN;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_NOT_ALLOWED;
import static co.infinum.queenofversions.InAppUpdateError.INSTALL_UNAVAILABLE;
import static co.infinum.queenofversions.InAppUpdateError.INTERNAL_ERROR;
import static co.infinum.queenofversions.InAppUpdateError.INVALID_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QueenOfVersionsTest {

    @Mock
    GoogleAppUpdater googleAppUpdater;

    @Mock
    QueenOfVersions.Callback updaterStateCallback;

    @Mock
    UpdateResult updateResult;

    @Mock
    UpdateInfo updateInfo;

    @Mock
    Storage storage;

    /**
     * Testing GoogleInAppUpdateCallback
     */

    @Test
    public void testGoogleCompleteUpdateCall() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.completeUpdate();

        verify(googleAppUpdater, times(1)).completeUpdate();
    }

    @Test
    public void testStartingGoogleInAppUpdaterWhenThereIsNewUpdate() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        UpdateResult updateResult = new UpdateResult(this.updateInfo, Collections.<String, String>emptyMap(),
                UpdateStatus.REQUIRED_UPDATE_NEEDED, 200);
        googleInAppUpdateCallback.onSuccess(updateResult);

        verify(googleAppUpdater, times(1)).initGoogleUpdate(true, 200, updateResult);
        verify(googleAppUpdater, times(0)).completeUpdate();
    }

    /**
     * If Prince returns that there is no updates, we still have to check with Google but offer it as non mandatory update?
     */

    @Test
    public void testStartingGoogleInAppUpdaterIfThereIsNoNewUpdate() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        UpdateResult updateResult = new UpdateResult(this.updateInfo, Collections.<String, String>emptyMap(),
                UpdateStatus.NO_UPDATE_AVAILABLE, 200);
        googleInAppUpdateCallback.onSuccess(updateResult);

        verify(googleAppUpdater, times(1)).initGoogleUpdate(false, 200, updateResult);
        verify(googleAppUpdater, times(0)).completeUpdate();
    }

    @Test
    public void testGettingErrorOnPrinceCallback() throws Throwable {
        OnPrinceOfVersionsError onError = Mockito.mock(OnPrinceOfVersionsError.class);
        OnPrinceOfVersionsSuccess onSuccess = Mockito.mock(OnPrinceOfVersionsSuccess.class);

        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback = new QueenOfVersionsUpdaterCallback(
                googleAppUpdater,
                updaterStateCallback,
                10,
                onSuccess,
                onError,
                new QueenOfVersions.OnUpdateNotAllowedReportNoUpdate(),
                new QueenOfVersions.OnInAppUpdateAvailableResumeWithCurrentResolution(),
                storage
        );
        Throwable error = new Throwable("Error");
        googleInAppUpdateCallback.onError(error);

        verify(onError, times(1)).continueUpdateCheckAsStatus(eq(error));
    }

    @Test
    public void testStatusChangedOnInstallStatusDownload() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.DOWNLOADED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusCanceled() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.CANCELED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(1)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusInstalling() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.INSTALLING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(1)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusInstalled() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.INSTALLED, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(1)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusPending() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.PENDING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(1)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusUnknown() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.UNKNOWN, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedOnInstallStatusDownloading() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.DOWNLOADING, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(1)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testStatusChangedInstallStatusRequiresUI() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.REQUIRES_UI_INTENT, InstallErrorCode.NO_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(0))
                .onDownloaded(any(QueenOfVersions.UpdateHandler.class), any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onCanceled();
        verify(updaterStateCallback, times(0)).onNoUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
        verify(updaterStateCallback, times(0)).onError(any(GoogleInAppUpdateException.class));
        verify(updaterStateCallback, times(0)).onDownloading(any(QueenOfVersionsInAppUpdateInfo.class), anyLong(), anyLong());
        verify(updaterStateCallback, times(0)).onInstalling(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onInstalled(any(QueenOfVersionsInAppUpdateInfo.class));
        verify(updaterStateCallback, times(0)).onPending(any(QueenOfVersionsInAppUpdateInfo.class));
    }

    @Test
    public void testErrorCodeOnApiNotAvailableError() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_API_NOT_AVAILABLE);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(API_NOT_AVAILABLE);
            }
        }));
    }

    @Test
    public void testErrorCodeOnDownloadNotPresent() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(DOWNLOAD_NOT_PRESENT);
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallNotAllowed() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(INSTALL_NOT_ALLOWED);
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallUnavailable() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_UNAVAILABLE);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(INSTALL_UNAVAILABLE);
            }
        }));
    }

    @Test
    public void testErrorCodeOnInternalError() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INTERNAL_ERROR);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(INTERNAL_ERROR);
            }
        }));
    }

    @Test
    public void testErrorCodeOnInvalidRequest() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INVALID_REQUEST);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(INVALID_REQUEST);
            }
        }));
    }

    @Test
    public void testErrorCodeOnUnknownError() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        InstallState installState = new MockInstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_UNKNOWN);
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onError(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.error().equals(ERROR_UNKNOWN);
            }
        }));
    }

    /**
     * Testing all possible Prince Of Versions and Google InApp Updates version permutations
     */

    @Test
    public void testPrinceAndGoogleSameVersionMandatory() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(11), 11, true, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE, updateResult);
    }

    @Test
    public void testPrinceAndGoogleSameVersionOptional() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(11), 11, false, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE, updateResult);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleMandatory() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(12), 11, true, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE, updateResult);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleOptional() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(12), 11, false, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE, updateResult);
    }

    @Test
    public void testPrinceHigherVersionThanGoogleMandatory() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        UpdateResult result = new UpdateResult(updateInfo, Collections.<String, String>emptyMap(), UpdateStatus.REQUIRED_UPDATE_NEEDED, 12);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(11), 12, true, result);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE, result);
    }

    @Test
    public void testPrinceHigherVersionThanGoogleOptional() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(12), 11, false, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE, updateResult);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleAndAppMandatory() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(12), 10, true, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.IMMEDIATE, updateResult);
    }

    @Test
    public void testPrinceLowerVersionThanGoogleAndAppOptional() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 10, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createAvailable(12), 11, false, updateResult);

        verify(googleAppUpdater, times(1)).startUpdate(AppUpdateType.FLEXIBLE, updateResult);
    }

    @Test
    public void testAppHigherVersionThanGoogleButLowerThanPrinceMandatory() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createUnavailable(), 12, true, updateResult);

        verify(googleAppUpdater, times(0)).startUpdate(anyInt(), eq(updateResult));
    }

    @Test
    public void testAppHigherVersionThanGoogleButLowerThanPrinceOptional() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createUnavailable(), 12, false, updateResult);

        verify(googleAppUpdater, times(0)).startUpdate(anyInt(), eq(updateResult));
    }

    /**
     * Testing GoogleInAppUpdateFailure listener
     */

    @Test
    public void testOnFailureCallInAppUpdateFailureListener() {
        GoogleInAppUpdateFailureListener googleInAppUpdateFailureListener = new GoogleInAppUpdateFailureListener(updaterStateCallback);
        googleInAppUpdateFailureListener.onFailure(new GoogleInAppUpdateException(ERROR_UNKNOWN));

        verify(updaterStateCallback, times(1)).onError(any(GoogleInAppUpdateException.class));
    }

    /**
     * Testing all possible on resume google calls
     */

    @Test
    public void testOnResumeHasBeenCalledForImmediate() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS, InstallStatus.CANCELED,
                false);

        verify(googleAppUpdater, times(0)).onUpdateDownloaded();
        verify(googleAppUpdater, times(1)).restartUpdate();
    }

    @Test
    public void testOnResumeHasBeenCallForFlexible() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.UNKNOWN, InstallStatus.DOWNLOADED, true);

        verify(googleAppUpdater, times(1)).onUpdateDownloaded();
        verify(googleAppUpdater, times(0)).restartUpdate();
    }

    @Test
    public void testNoUpdateFromGoogleOrPrince() {
        QueenOfVersionsUpdaterCallback googleInAppUpdateCallback =
                new QueenOfVersionsUpdaterCallback(googleAppUpdater, updaterStateCallback, 11, storage);

        UpdateResult result = new UpdateResult(updateInfo, Collections.<String, String>emptyMap(), UpdateStatus.NO_UPDATE_AVAILABLE, 11);
        googleInAppUpdateCallback.handleSuccess(MockAppUpdateData.createUnavailable(), 11, true, result);

        verify(googleAppUpdater, times(1)).noUpdate(ArgumentMatchers.<String, String>anyMap(), any(UpdateInfo.class));
    }
}
