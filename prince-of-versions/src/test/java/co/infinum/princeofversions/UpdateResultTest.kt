package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateResultTest {

    private val updateVersion = 2
    private val metadata = mapOf<String, String>()
    private val status = UpdateStatus.NEW_UPDATE_AVAILABLE
    private val info = UpdateInfo(1, 2, emptyMap(), updateVersion, NotificationType.ALWAYS)

    @Test
    fun testConstructorAndGetters() {
        val result = UpdateResult(info, metadata, status, updateVersion)

        assertThat(result.info).isEqualTo(info)
        assertThat(result.metadata).isEqualTo(metadata)
        assertThat(result.status).isEqualTo(status)
        assertThat(result.updateVersion).isEqualTo(updateVersion)
    }

    @Test
    fun testEqualsAndHashCodeContract() {
        val result1 = UpdateResult(info, metadata, status, updateVersion)
        val result2 = UpdateResult(info, metadata, status, updateVersion)
        val differentStatus = UpdateResult(info, metadata, UpdateStatus.NO_UPDATE_AVAILABLE, updateVersion)
        val differentVersion = UpdateResult(info, metadata, status, updateVersion + 1)

        // Reflexive
        assertThat(result1).isEqualTo(result1)
        // Symmetric
        assertThat(result1).isEqualTo(result2)
        assertThat(result2).isEqualTo(result1)
        // Consistent HashCode for equal objects
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode())

        // Not equals for different status
        assertThat(result1).isNotEqualTo(differentStatus)

        // Not equals for different updateVersion
        assertThat(result1).isNotEqualTo(differentVersion)

        // Different hash code for different version
        assertThat(result1.hashCode()).isNotEqualTo(differentVersion.hashCode())

        // Not equals for different types
        assertThat(result1.equals("a string")).isFalse()
        // Not equals for null
        assertThat(result1.equals(null)).isFalse()
    }

    @Test
    fun testToString() {
        val result = UpdateResult(info, metadata, status, updateVersion)

        assertThat(result.toString()).contains("metadata=")
            .contains("info=")
            .contains("status=")
            .contains("updateVersion=")
    }
}
