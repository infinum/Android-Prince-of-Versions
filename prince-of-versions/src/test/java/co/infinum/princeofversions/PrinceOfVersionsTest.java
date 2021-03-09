package co.infinum.princeofversions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import co.infinum.princeofversions.mocks.MockApplicationConfiguration;
import co.infinum.princeofversions.mocks.MockDefaultRequirementChecker;
import co.infinum.princeofversions.mocks.MockStorage;
import co.infinum.princeofversions.mocks.ResourceFileLoader;
import co.infinum.princeofversions.mocks.SingleThreadExecutor;

import static co.infinum.princeofversions.PrinceOfVersionsDefaultRequirementsChecker.KEY;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PrinceOfVersionsTest {

    private static final String DEFAULT_OS_VERSION = KEY;

    @Mock
    UpdaterCallback callback;

    private static Throwable anyThrowable() {
        return any(Throwable.class);
    }

    @Test
    public void testCheckingValidContentNoNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_no_notification.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentNoNotificationSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_no_notification.json"));
        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentNotificationAlways() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentNotificationAlwaysSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_notification_always.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentNotificationAlwaysAlreadyNotified() {
        Storage storage = new MockStorage(245);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentNotificationAlwaysAlreadyNotifiedSync() throws Throwable {
        Storage storage = new MockStorage(245);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_notification_always.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentOnlyMinVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_only_min_version.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingValidContentOnlyMinVersionSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_only_min_version.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(200);
    }

    @Test
    public void testCheckingValidContentWithoutCodes() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingValidContentWithoutCodesSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptionalSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsLessThanMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(100,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsLessThanMinAndLessThanOptionalSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(100,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(123,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptionalSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(123,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptionalButAlreadyNotified() {
        Storage storage = new MockStorage(245);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(123,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptionalButAlreadyNotifiedSync() throws Throwable {
        Storage storage = new MockStorage(245);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(123,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(245,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptionalSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(245,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptionalSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(300);
    }

    @Test(expected = Throwable.class)
    public void testCheckingInvalidContentWithInvalidVersionSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("invalid_update_invalid_version.json"));
    }

    @Test
    public void testCheckingInvalidContentNoAndroidKey() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(
            300, 16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("invalid_update_no_android.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingInvalidContentNoAndroidKeySync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("invalid_update_no_android.json"));
    }

    @Test
    public void testCheckingInvalidContentNoJSON() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("invalid_update_no_android.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingInvalidContentNoJSONSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("invalid_update_no_android.json"));
    }

    @Test
    public void testCheckingValidContentWithAlwaysNotification() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_notification_always.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingValidContentWithAlwaysNotificationSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_notification_always.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(300);
    }

    @Test
    public void testCheckingValidContentWithOnlyMinVersion() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_only_min_version.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingValidContentWithOnlyMinVersionSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(300,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_only_min_version.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(300);
    }

    @Test
    public void testCheckingWhenVersionIsAlreadyNotified() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(245,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingWhenVersionIsAlreadyNotifiedSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(245,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
    }

    @Test
    public void testCheckingWhenUpdateShouldBeMade() {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_no_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingWhenUpdateShouldBeMadeSync() throws Throwable {
        Storage storage = new MockStorage();
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), new MockApplicationConfiguration(200,
            16));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_no_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingUpdateWithFullSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(),
            appConfig, Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingUpdateWithFullSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingUpdateWithASingleSdkValue() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(200, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingUpdateWithASingleSdkValueSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(200, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_single_sdk_value.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingUpdateWithHugeSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_huge_sdk_values.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingUpdateWithHugeSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_huge_sdk_values.json"));
    }

    @Test
    public void testCheckingUpdateWithDowngradingSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingUpdateWithDowngradingSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(100, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithDowngradingSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(241, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithDowngradingSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(241, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_downgrading_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithHugeSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(241, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_huge_sdk_values.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingOptionalUpdateWithHugeSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration appConfig = new MockApplicationConfiguration(241, 16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(appConfig);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), appConfig,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_huge_sdk_values.json"));
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValue() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(241,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValueSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(241,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_single_sdk_value.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValueAndHigherInitialVersion() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(241,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_single_sdk_value.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSingleSdkValueAndHigherInitialVersionSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(241,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_single_sdk_value.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingMandatoryUpdateWithSdkValues() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(140,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_mandatory_update.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingMandatoryUpdateWithSdkValuesSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(140,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_mandatory_update.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndTheSameVersions() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(245,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndTheSameVersionsSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(245,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndWithDifferentVersions() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(244,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndWithDifferentVersionsSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(244,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full_b_with_sdk_values.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(245);
    }

    @Test
    public void testCheckingOptionalUpdateWithSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(230,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_full_with_sdk_values.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test
    public void testCheckingForUpdateWhenRequirementsAreNull() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(230,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("update_with_requirements_null.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingOptionalUpdateWithSdkValuesAndIncreaseInMinorSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(230,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_full_with_sdk_values.json"));
    }

    @Test
    public void testCheckingOptionalUpdateWithHighSdkValuesAndIncreaseInMinor() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(230,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_increase_in_sdk_values.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingOptionalUpdateWithHighSdkValuesAndIncreaseInMinorSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(230,
            16);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_big_increase_in_sdk_values.json"));
    }

    @Test
    public void testCheckingMandatoryUpdateWithDeviceThatHasVeryLowMinSdk() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(100,
            12);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_same_sdk_values.json"),
            callback
        );

        verify(callback, times(0)).onSuccess(any(UpdateResult.class));
        verify(callback, times(1)).onError(anyThrowable());
    }

    @Test(expected = Throwable.class)
    public void testCheckingMandatoryUpdateWithDeviceThatHasVeryLowMinSdkSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(100,
            12);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_same_sdk_values.json"));
    }

    @Test
    public void testCheckingOptionalUpdateWithBigMinimumVersionMinSdk() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(200,
            23);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(211);
    }

    @Test
    public void testCheckingOptionalUpdateWithBigMinimumVersionMinSdkSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(200, 23);
        PrinceOfVersions princeOfVersions = new PrinceOfVersions(
            storage,
            new SingleThreadExecutor(),
            applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(
                DEFAULT_OS_VERSION,
                new MockDefaultRequirementChecker(applicationConfiguration)
            )
        );
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk"
            + ".json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
        assertThat(result.getUpdateVersion()).isEqualTo(211);
        assertThat(storage.lastNotifiedVersion(null)).isEqualTo(211);
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDate() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(211,
            20);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDateSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(211,
            20);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_with_big_minimum_version_min_sdk"
            + ".json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(211);
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDateAndNoMandatoryUpdateIsNotDefined() {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(211,
            20);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        princeOfVersions.checkForUpdatesInternal(
            new SingleThreadExecutor(),
            new ResourceFileLoader("valid_update_no_min_version.json"),
            callback
        );

        verify(callback, times(1)).onSuccess(any(UpdateResult.class));
        verify(callback, times(0)).onError(anyThrowable());
    }

    @Test
    public void testCheckingOptionalUpdateWhenUserIsUpToDateAndNoMandatoryUpdateIsNotDefinedSync() throws Throwable {
        Storage storage = new MockStorage();
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(211,
            20);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);

        PrinceOfVersions princeOfVersions = new PrinceOfVersions(storage, new SingleThreadExecutor(), applicationConfiguration,
            Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        UpdateResult result = princeOfVersions.checkForUpdates(new ResourceFileLoader("valid_update_no_min_version.json"));

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
        assertThat(result.getUpdateVersion()).isEqualTo(245);
    }
}