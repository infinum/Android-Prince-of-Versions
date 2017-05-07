package co.infinum.princeofversions.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.Storage;
import co.infinum.princeofversions.UpdaterCallback;
import co.infinum.princeofversions.mocks.MockApplicationConfiguration;
import co.infinum.princeofversions.mocks.MockStorage;
import co.infinum.princeofversions.mocks.ResourceFileLoader;
import co.infinum.princeofversions.mocks.SingleThreadExecutor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PrinceOfVersionsTest {

    private UpdaterCallback callback;

    @Before
    public void setUp() {
        callback = Mockito.mock(UpdaterCallback.class);
    }

    @Test
    public void testCheckingValidContentNoNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_no_notification.json"),
                callback
        );
        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingValidContentNotificationAlways() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_notification_always.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingValidContentNotificationAlwaysAlreadyNotified() {
        Storage storage = new MockStorage("2.4.5");
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_notification_always.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingValidContentOnlyMinVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_only_min_version.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingValidContentWithoutCodes() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsLessThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.2.3", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptionalButAlreadyNotified() {
        Storage storage = new MockStorage("2.4.5");
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.2.3", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.5", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentWithInvalidVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("invalid_update_invalid_version.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentNoAndroidKey() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("invalid_update_no_android.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingInvalidContentNoJSON() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("invalid_update_no_android.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingValidContentWithAlwaysNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_notification_always.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingValidContentWithOnlyMinVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("3.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_only_min_version.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingWhenVersionIsAlreadyNotified() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.5", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingWhenCurrentAppVersionIsInvalid() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingWhenUpdateShouldBeMade() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_no_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingUpdateWithFullSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingUpdateWithASingleSdkValue() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingUpdateWithHugeSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_huge_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(1)).onNoUpdate(anyMap());
    }

    @Test
    public void testCheckingUpdateWithDowngradingSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.0.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        verify(callback, times(0)).onNoUpdate(anyMap());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingOptionalUpdateWithDowngradingSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingOptionalUpdateWithHugeSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_huge_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValue() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValueAndHigherInitialVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.1", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(false), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingMandatoryUpdateWithSdkValues() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.4.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_mandatory_update.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5"), eq(true), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5"));
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndTheSameVersions() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.5", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndWithDifferentVersions() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.4.4", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.4.5-b45"), eq(false), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.4.5-b45"));
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.3.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_full_with_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithHighSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.3.0", 16));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_big_increase_in_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingMandatoryUpdateWithDeviceThatHasVeryLowMinSdk() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.0.0", 12));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_same_sdk_values.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingMandatoryUpdateWithUnavailableOptionalUpdate() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("1.2.0", 14));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_small_sdk_values.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("1.2.3"), eq(true), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("1.2.3"));
    }

    @Test
    public void testCheckingOptionalUpdateWithBigMinimumVersionMinSdk() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.0.0", 23));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
                callback
        );

        verify(callback, times(1)).onNewUpdate(eq("2.1.1"), eq(false), anyMap());
        verify(callback, times(0)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null).equals("2.1.1"));
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDate() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new MockApplicationConfiguration("2.1.1", 20));
        princeOfVersions.checkForUpdatesInternal(
                new SingleThreadExecutor(),
                new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
                callback
        );

        verify(callback, times(0)).onNewUpdate(anyString(), anyBoolean(), anyMap());
        verify(callback, times(1)).onNoUpdate(anyMap());
        verify(callback, times(0)).onError(anyThrowable());
    }

    private static Throwable anyThrowable() {
        return any(Throwable.class);
    }

    private static Map<String, String> anyMap() {
        return ArgumentMatchers.anyMap();
    }

}