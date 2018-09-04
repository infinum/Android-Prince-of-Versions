package co.infinum.princeofversions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CheckResultTest {

    private static final String DEFAULT_VERSION = "version";

    private static final Map<String, String> DEFAULT_METADATA = new HashMap<>();

    @Test
    public void checkHasUpdateMandatory() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalAlways() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalOnce() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateMandatoryWithNulls() {
        CheckResult result = CheckResult.mandatoryUpdate(null, null);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalAlwaysWithNulls() {
        CheckResult result = CheckResult.optionalUpdate(null, NotificationType.ALWAYS, null);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasUpdateOptionalOnceWithNulls() {
        CheckResult result = CheckResult.optionalUpdate(null, NotificationType.ONCE, null);

        assertThat(result.hasUpdate()).isTrue();
    }

    @Test
    public void checkHasNoUpdate() {
        CheckResult result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.hasUpdate()).isFalse();
    }

    @Test
    public void checkHasNoUpdateWithNulls() {
        CheckResult result = CheckResult.noUpdate(null, null);

        assertThat(result.hasUpdate()).isFalse();
    }

    @Test
    public void checkGetUpdateVersion() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.getUpdateVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    public void checkIsOptionalMandatory() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.isOptional()).isFalse();
    }

    @Test
    public void checkIsOptionalOptionalAlways() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA);

        assertThat(result.isOptional()).isTrue();
    }

    @Test
    public void checkIsOptionalOptionalOnce() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA);

        assertThat(result.isOptional()).isTrue();
    }

    @Test
    public void checkIsOptionalMandatoryWithNulls() {
        CheckResult result = CheckResult.mandatoryUpdate(null, null);

        assertThat(result.isOptional()).isFalse();
    }

    @Test
    public void checkIsOptionalOptionalAlwaysWithNulls() {
        CheckResult result = CheckResult.optionalUpdate(null, NotificationType.ALWAYS, null);

        assertThat(result.isOptional()).isTrue();
    }

    @Test
    public void checkIsOptionalOptionalOnceWithNulls() {
        CheckResult result = CheckResult.optionalUpdate(null, NotificationType.ONCE, null);

        assertThat(result.isOptional()).isTrue();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkIsOptionalWhenNoUpdate() {
        CheckResult result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.isOptional()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkIsOptionalWhenNoUpdateWithNulls() {
        CheckResult result = CheckResult.noUpdate(null, null);

        assertThat(result.isOptional()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNotificationTypeMandatory() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        result.getNotificationType();
    }

    @Test
    public void checkNotificationTypelOptionalAlways() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA);

        assertThat(result.getNotificationType()).isEqualTo(NotificationType.ALWAYS);
    }

    @Test
    public void checkNotificationTypeOptionalOnce() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA);

        assertThat(result.getNotificationType()).isEqualTo(NotificationType.ONCE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNotificationTypeWhenNoUpdate() {
        CheckResult result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        result.getNotificationType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNotificationTypeWhenNoUpdateWithNulls() {
        CheckResult result = CheckResult.noUpdate(null, null);

        result.getNotificationType();
    }

    @Test
    public void checkStatusMandatory() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.status()).isEqualTo(UpdateStatus.MANDATORY);
    }

    @Test
    public void checkStatusOptional() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA);

        assertThat(result.status()).isEqualTo(UpdateStatus.OPTIONAL);
    }

    @Test
    public void checkStatusNoUpdate() {
        CheckResult result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.status()).isEqualTo(UpdateStatus.NO_UPDATE);
    }

    @Test
    public void checkMetadataMandatory() {
        CheckResult result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA);
    }

    @Test
    public void checkMetadataOptional() {
        CheckResult result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA);

        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA);
    }

    @Test
    public void checkMetadataNoUpdate() {
        CheckResult result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA);

        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA);
    }
}
