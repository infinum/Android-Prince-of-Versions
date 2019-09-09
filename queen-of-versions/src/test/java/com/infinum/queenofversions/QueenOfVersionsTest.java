package com.infinum.queenofversions;

import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallErrorCode;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.infinum.queenofversions.mocks.ResourceFileLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.mocks.MockApplicationConfiguration;
import co.infinum.princeofversions.mocks.MockStorage;
import co.infinum.princeofversions.mocks.SingleThreadExecutor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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
    UpdaterStateCallback updaterStateCallback;

    private static Throwable anyThrowable() {
        return any(Throwable.class);
    }

    private static Map<String, String> anyMap() {
        return ArgumentMatchers.anyMap();
    }

    /**
     * Testing Prince of Version with new GoogleInAppUpdateCallback
     */

    @Test
    public void testCheckingValidContentNoNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_no_notification.json"),
            googleInAppUpdateCallback
        );
        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingValidContentNotificationAlwaysAlreadyNotified() {
        Storage storage = new MockStorage("2.4.5");
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingValidContentNotificationAlways() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingValidContentOnlyMinVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_only_min_version.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingValidContentWithoutCodes() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsLessThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.2.3", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptionalButAlreadyNotified() {
        Storage storage = new MockStorage("2.4.5");
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.2.3", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.5", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentWithInvalidVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("invalid_update_invalid_version.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentNoAndroidKey() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("invalid_update_no_android.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentNoJSON() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("invalid_update_no_android.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingValidContentWithAlwaysNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDateAndNoMandatoryUpdateIsNotDefined() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.1.1", 20));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_no_min_version.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(anyString(), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDate() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.1.1", 20));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithBigMinimumVersionMinSdk() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.0.0", 23));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.1.1"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.1.1");
    }

    @Test
    public void testCheckingMandatoryUpdateWithUnavailableOptionalUpdate() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.2.0", 14));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_small_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("1.2.3"), eq(true), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("1.2.3");
    }

    @Test
    public void testCheckingMandatoryUpdateWithDeviceThatHasVeryLowMinSdk() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.0.0", 12));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_same_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithHighSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.3.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_increase_in_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.3.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_with_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndWithDifferentVersions() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.4", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5-b45"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5-b45");
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndTheSameVersions() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.5", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingMandatoryUpdateWithSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("1.4.0", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_mandatory_update.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValueAndHigherInitialVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValue() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(googleInAppUpdateCallback, times(0)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo("2.4.5");
    }

    @Test
    public void testCheckingOptionalUpdateWithHugeSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions =
            new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_huge_sdk_values.json"),
            googleInAppUpdateCallback
        );

        verify(googleInAppUpdateCallback, times(0)).onNewUpdate(eq("2.4.1"), anyBoolean(), anyMap());
        verify(googleInAppUpdateCallback, times(1)).onNoUpdate(anyMap());
        verify(googleInAppUpdateCallback, times(0)).onError(anyThrowable());
    }

    /**
     * Testing GoogleInAppUpdateCallback
     */

    @Test
    public void testGoogleCompleteUpdateCall() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        googleInAppUpdateCallback.completeUpdate();

        verify(googleAppUpdater,times(1)).completeUpdate();
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
        InstallState installState = new InstallState(InstallStatus.DOWNLOADED, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.CANCELED, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.INSTALLING, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.INSTALLED, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.PENDING, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.UNKNOWN, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.DOWNLOADING, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.REQUIRES_UI_INTENT, InstallErrorCode.NO_ERROR, "testing");
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
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_API_NOT_AVAILABLE, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.API_NOT_AVAILABLE;
            }
        }));
    }

    @Test
    public void testErrorCodeOnDownloadNotPresent() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.DOWNLOAD_NOT_PRESENT;
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallNotAllowed() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.INSTALL_NOT_ALLOWED;
            }
        }));
    }

    @Test
    public void testErrorCodeOnInstallUnavailable() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INSTALL_UNAVAILABLE, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.INSTALL_UNAVAILABLE;
            }
        }));
    }

    @Test
    public void testErrorCodeOnInternalError() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INTERNAL_ERROR, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.INTERNAL_ERROR;
            }
        }));
    }

    @Test
    public void testErrorCodeOnInvalidRequest() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_INVALID_REQUEST, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.INVALID_REQUEST;
            }
        }));
    }

    @Test
    public void testErrorCodeOnUnknownError() {
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 10);
        InstallState installState = new InstallState(InstallStatus.FAILED, InstallErrorCode.ERROR_UNKNOWN, "testing");
        googleInAppUpdateCallback.onStateUpdate(installState);

        verify(updaterStateCallback, times(1)).onFailed(argThat(new ArgumentMatcher<GoogleInAppUpdateException>() {
            @Override
            public boolean matches(GoogleInAppUpdateException argument) {
                return argument.getError() == GoogleException.ERROR_UNKNOWN;
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

        verify(googleAppUpdater, times(0)).startUpdate(anyInt());
        verify(googleAppUpdater, times(1)).noUpdate();
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
    public void testOnFailureCallInAppUpdateFailureListener(){
        GoogleInAppUpdateFailureListener googleInAppUpdateFailureListener = new GoogleInAppUpdateFailureListener(updaterStateCallback);
        googleInAppUpdateFailureListener.onFailure(new GoogleInAppUpdateException(new Throwable("Error")));

        verify(updaterStateCallback,times(1)).onFailed(any(GoogleInAppUpdateException.class));
    }

    /**
     * Testing all possible on resume google calls
     */

    @Test
    public void testOnResumeHasBeenCalledForImmediate(){
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,InstallStatus.CANCELED,
            false);

        verify(googleAppUpdater, times(0)).notifyUser();
        verify(googleAppUpdater, times(1)).restartUpdate();
    }

    @Test
    public void testOnResumeHasBeenCallForFlexible(){
        GoogleInAppUpdateCallback googleInAppUpdateCallback =
            new GoogleInAppUpdateCallback(200, googleAppUpdater, updaterStateCallback, 11);
        googleInAppUpdateCallback.handleResumeSuccess(UpdateAvailability.UNKNOWN,InstallStatus.DOWNLOADED,true);

        verify(googleAppUpdater, times(1)).notifyUser();
        verify(googleAppUpdater, times(0)).restartUpdate();
    }

}
