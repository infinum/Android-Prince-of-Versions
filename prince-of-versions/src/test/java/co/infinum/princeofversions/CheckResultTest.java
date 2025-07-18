package co.infinum.princeofversions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CheckResultTest {

    private static final int DEFAULT_REQUIRED_VERSION = 1;

    private static final int DEFAULT_LAST_VERSION_AVAILABLE = 1;

    private static final Map<String, String> DEFAULT_REQUIREMENTS = new HashMap<>();

    private static final int DEFAULT_VERSION = 1;

    private static final Map<String, String> DEFAULT_METADATA = new HashMap<>();

    private static final UpdateInfo updateInfo = new UpdateInfo(DEFAULT_REQUIRED_VERSION, DEFAULT_LAST_VERSION_AVAILABLE,
        DEFAULT_REQUIREMENTS, DEFAULT_VERSION, NotificationType.ALWAYS);

    @Test
    public void checkHasUpdateMandatory() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalAlways() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalOnce() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasNoUpdate() {
        CheckResult result = CheckResult.Companion.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.hasUpdate()).isFalse();
    }

    @Test
    public void checkGetUpdateVersion() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getUpdateVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    public void checkIsOptionalMandatory() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.isOptional()).isFalse();
    }

    @Test
    public void checkIsOptionalOptionalAlways() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.isOptional()).isTrue();
    }

    @Test
    public void checkIsOptionalOptionalOnce() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo);

        assertThat(result.isOptional()).isTrue();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkIsOptionalWhenNoUpdate() {
        CheckResult result = CheckResult.Companion.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        result.isOptional();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNotificationTypeMandatory() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        result.safeNotificationType();
    }

    @Test
    public void checkNotificationTypeOptionalAlways() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getNotificationType()).isEqualTo(NotificationType.ALWAYS);
    }

    @Test
    public void checkNotificationTypeOptionalOnce() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo);

        assertThat(result.getNotificationType()).isEqualTo(NotificationType.ONCE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNotificationTypeWhenNoUpdate() {
        CheckResult result = CheckResult.Companion.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        result.safeNotificationType();
    }

    @Test
    public void checkStatusMandatory() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED);
    }

    @Test
    public void checkStatusOptional() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE);
    }

    @Test
    public void checkStatusNoUpdate() {
        CheckResult result = CheckResult.Companion.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getStatus()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE);
    }

    @Test
    public void checkMetadataMandatory() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getMetadata()).isEqualTo(DEFAULT_METADATA);
    }

    @Test
    public void checkMetadataOptional() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getMetadata()).isEqualTo(DEFAULT_METADATA);
    }

    @Test
    public void checkMetadataNoUpdate() {
        CheckResult result = CheckResult.Companion.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getMetadata()).isEqualTo(DEFAULT_METADATA);
    }

    @Test
    public void checkUpdateInfoMandatoryUpdate() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo()).isEqualTo(updateInfo);
    }

    @Test
    public void checkUpdateInfoOptionalUpdate() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo()).isEqualTo(updateInfo);
    }

    @Test
    public void checkUpdateInfoRequiredVersionMandatoryUpdate() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getRequiredVersion()).isEqualTo(DEFAULT_REQUIRED_VERSION);
    }

    @Test
    public void checkUpdateInfoRequiredVersionOptionalUpdate() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getRequiredVersion()).isEqualTo(DEFAULT_REQUIRED_VERSION);
    }

    @Test
    public void checkUpdateInfoLastVersionAvailableMandatoryUpdate() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getLastVersionAvailable()).isEqualTo(DEFAULT_LAST_VERSION_AVAILABLE);
    }

    @Test
    public void checkUpdateInfoLastVersionAvailableOptionalUpdate() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getLastVersionAvailable()).isEqualTo(DEFAULT_LAST_VERSION_AVAILABLE);
    }

    @Test
    public void checkUpdateInfoInstalledVersionMandatoryUpdate() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getInstalledVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    public void checkUpdateInfoInstalledVersionOptionalUpdate() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getInstalledVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    public void checkUpdateInfoRequirementsMandatoryUpdate() {
        CheckResult result = CheckResult.Companion.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getRequirements()).isEqualTo(DEFAULT_REQUIREMENTS);
    }

    @Test
    public void checkUpdateInfoRequirementsOptionalUpdate() {
        CheckResult result = CheckResult.Companion.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo);

        assertThat(result.getInfo().getRequirements()).isEqualTo(DEFAULT_REQUIREMENTS);
    }
}