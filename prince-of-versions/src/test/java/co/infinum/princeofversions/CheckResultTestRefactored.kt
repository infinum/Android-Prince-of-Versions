package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class CheckResultTestRefactored {

    private companion object Companion {
        private const val DEFAULT_REQUIRED_VERSION = 1
        private const val DEFAULT_LAST_VERSION_AVAILABLE = 1
        private val DEFAULT_REQUIREMENTS: Map<String, String> = emptyMap()
        private const val DEFAULT_VERSION = 1
        private val DEFAULT_METADATA: Map<String, String> = emptyMap()
        private val updateInfo = UpdateInfo(
            DEFAULT_REQUIRED_VERSION,
            DEFAULT_LAST_VERSION_AVAILABLE,
            DEFAULT_REQUIREMENTS,
            DEFAULT_VERSION,
            NotificationType.ALWAYS
        )
    }

    @Test
    fun checkHasUpdateMandatory() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasUpdateOptionalAlways() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasUpdateOptionalOnce() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasUpdateMandatoryWithNulls() {
        val result = CheckResult.mandatoryUpdate(null, null, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasUpdateOptionalAlwaysWithNulls() {
        val result = CheckResult.optionalUpdate(null, NotificationType.ALWAYS, null, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasUpdateOptionalOnceWithNulls() {
        val result = CheckResult.optionalUpdate(null, NotificationType.ONCE, null, updateInfo)
        assertThat(result.hasUpdate()).isTrue()
    }

    @Test
    fun checkHasNoUpdate() {
        val result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.hasUpdate()).isFalse()
    }

    @Test
    fun checkHasNoUpdateWithNulls() {
        val result = CheckResult.noUpdate(null, null, updateInfo)
        assertThat(result.hasUpdate()).isFalse()
    }

    @Test
    fun checkGetUpdateVersion() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.updateVersion).isEqualTo(DEFAULT_VERSION)
    }

    @Test
    fun checkIsOptionalMandatory() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.isOptional).isFalse()
    }

    @Test
    fun checkIsOptionalOptionalAlways() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.isOptional).isTrue()
    }

    @Test
    fun checkIsOptionalOptionalOnce() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo)
        assertThat(result.isOptional).isTrue()
    }

    @Test
    fun checkIsOptionalMandatoryWithNulls() {
        val result = CheckResult.mandatoryUpdate(null, null, updateInfo)
        assertThat(result.isOptional).isFalse()
    }

    @Test
    fun checkIsOptionalOptionalAlwaysWithNulls() {
        val result = CheckResult.optionalUpdate(null, NotificationType.ALWAYS, null, updateInfo)
        assertThat(result.isOptional).isTrue()
    }

    @Test
    fun checkIsOptionalOptionalOnceWithNulls() {
        val result = CheckResult.optionalUpdate(null, NotificationType.ONCE, null, updateInfo)
        assertThat(result.isOptional).isTrue()
    }

    @Test
    fun checkIsOptionalWhenNoUpdate() {
        val result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThatThrownBy { result.isOptional }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no update available.")
    }

    @Test
    fun checkIsOptionalWhenNoUpdateWithNulls() {
        val result = CheckResult.noUpdate(null, null, updateInfo)
        assertThatThrownBy { result.isOptional }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no update available.")
    }

    @Test
    fun checkNotificationTypeMandatory() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThatThrownBy { result.notificationType }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no optional update available.")
    }

    @Test
    fun checkNotificationTypelOptionalAlways() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.notificationType).isEqualTo(NotificationType.ALWAYS)
    }

    @Test
    fun checkNotificationTypeOptionalOnce() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ONCE, DEFAULT_METADATA, updateInfo)
        assertThat(result.notificationType).isEqualTo(NotificationType.ONCE)
    }

    @Test
    fun checkNotificationTypeWhenNoUpdate() {
        val result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThatThrownBy { result.notificationType }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no update available.")
    }

    @Test
    fun checkNotificationTypeWhenNoUpdateWithNulls() {
        val result = CheckResult.noUpdate(null, null, updateInfo)
        assertThatThrownBy { result.notificationType }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no update available.")
    }

    @Test
    fun checkStatusMandatory() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.status()).isEqualTo(UpdateStatus.REQUIRED_UPDATE_NEEDED)
    }

    @Test
    fun checkStatusOptional() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.status()).isEqualTo(UpdateStatus.NEW_UPDATE_AVAILABLE)
    }

    @Test
    fun checkStatusNoUpdate() {
        val result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.status()).isEqualTo(UpdateStatus.NO_UPDATE_AVAILABLE)
    }

    @Test
    fun checkMetadataMandatory() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA)
    }

    @Test
    fun checkMetadataOptional() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA)
    }

    @Test
    fun checkMetadataNoUpdate() {
        val result = CheckResult.noUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.metadata()).isEqualTo(DEFAULT_METADATA)
    }

    @Test
    fun checkUpdateInfoMandatoryUpdate() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.info).isEqualTo(updateInfo)
    }

    @Test
    fun checkUpdateInfoOptionalUpdate() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.info).isEqualTo(updateInfo)
    }

    @Test
    fun checkUpdateInfoRequiredVersionMandatoryUpdate() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.requiredVersion).isEqualTo(DEFAULT_REQUIRED_VERSION)
    }

    @Test
    fun checkUpdateInfoRequiredVersionOptionalUpdate() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.requiredVersion).isEqualTo(DEFAULT_REQUIRED_VERSION)
    }

    @Test
    fun checkUpdateInfoLastVersionAvailableMandatoryUpdate() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.lastVersionAvailable).isEqualTo(DEFAULT_LAST_VERSION_AVAILABLE)
    }

    @Test
    fun checkUpdateInfoLastVersionAvailableOptionalUpdate() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.lastVersionAvailable).isEqualTo(DEFAULT_LAST_VERSION_AVAILABLE)
    }

    @Test
    fun checkUpdateInfoInstalledVersionMandatoryUpdate() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.installedVersion).isEqualTo(DEFAULT_VERSION)
    }

    @Test
    fun checkUpdateInfoInstalledVersionOptionalUpdate() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.installedVersion).isEqualTo(DEFAULT_VERSION)
    }

    @Test
    fun checkUpdateInfoRequirementsMandatoryUpdate() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.requirements).isEqualTo(DEFAULT_REQUIREMENTS)
    }

    @Test
    fun checkUpdateInfoRequirementsOptionalUpdate() {
        val result = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        assertThat(result.info.requirements).isEqualTo(DEFAULT_REQUIREMENTS)
    }

    @Test
    fun checkEqualsAndHashCodeContract() {
        val mandatoryResult1 = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        val mandatoryResult2 = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        val mandatoryResult3 = CheckResult.mandatoryUpdate(2, DEFAULT_METADATA, updateInfo)
        val optionalResult1 = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)
        val optionalResult2 = CheckResult.optionalUpdate(DEFAULT_VERSION, NotificationType.ALWAYS, DEFAULT_METADATA, updateInfo)

        // Test equals contract
        assertThat(mandatoryResult1).isEqualTo(mandatoryResult1) // Reflexive
        assertThat(mandatoryResult1).isEqualTo(mandatoryResult2) // Symmetric
        assertThat(mandatoryResult2).isEqualTo(mandatoryResult1)
        assertThat(mandatoryResult1).isNotEqualTo(mandatoryResult3)
        assertThat(mandatoryResult1).isNotEqualTo(optionalResult1)
        assertThat(mandatoryResult1.equals("a string")).isFalse()
        assertThat(mandatoryResult1.equals(null)).isFalse()

        // Test hashCode contract
        // For optional updates, hashcode should be consistent
        assertThat(optionalResult1.hashCode()).isEqualTo(optionalResult2.hashCode())

        // For mandatory updates, hashCode throws an exception - this is a bug in the class, but the test should document it
        // TODO - check whether this bug is of real concern in the actual implementation and context of usage - if so fix it
        assertThatThrownBy { mandatoryResult1.hashCode() }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("There is no optional update available.")
    }

    @Test
    fun checkToStringContent() {
        val result = CheckResult.mandatoryUpdate(DEFAULT_VERSION, DEFAULT_METADATA, updateInfo)
        val resultString = result.toString()

        assertThat(resultString).contains(UpdateStatus.REQUIRED_UPDATE_NEEDED.toString())
        assertThat(resultString).contains(updateInfo.toString())
        assertThat(resultString).contains(DEFAULT_METADATA.toString())
        assertThat(resultString).contains("null") // notificationType is null for mandatory
    }
}