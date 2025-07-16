package co.infinum.princeofversions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateInfoTest {

    private val requiredVersion: Int? = 2
    private val lastVersionAvailable: Int? = 3
    private val installedVersion: Int = 1
    private val notificationType: NotificationType = NotificationType.ALWAYS
    private val requirements: Map<String, String> = emptyMap()

    @Test
    fun testConstructorAndGetters() {
        val info = UpdateInfo(requiredVersion, lastVersionAvailable, requirements, installedVersion, notificationType)

        assertThat(info.requiredVersion).isEqualTo(requiredVersion)
        assertThat(info.lastVersionAvailable).isEqualTo(lastVersionAvailable)
        assertThat(info.requirements).isEqualTo(requirements)
        assertThat(info.installedVersion).isEqualTo(installedVersion)
        assertThat(info.notificationFrequency).isEqualTo(notificationType)
    }

    @Test
    fun testEqualsAndHashCodeContract() {
        val info1 = UpdateInfo(requiredVersion, lastVersionAvailable, requirements, installedVersion, notificationType)
        val info2 = UpdateInfo(requiredVersion, lastVersionAvailable, requirements, installedVersion, notificationType)
        val info3 = UpdateInfo(requiredVersion, 4, requirements, installedVersion, notificationType)
        val infoWithNulls1 = UpdateInfo(null, null, requirements, installedVersion, notificationType)
        val infoWithNulls2 = UpdateInfo(null, null, requirements, installedVersion, notificationType)

        // Reflexive
        assertThat(info1).isEqualTo(info1)
        // Symmetric
        assertThat(info1).isEqualTo(info2)
        assertThat(info2).isEqualTo(info1)
        // Consistent HashCode
        assertThat(info1.hashCode()).isEqualTo(info2.hashCode())
        // Not equals for different values
        assertThat(info1).isNotEqualTo(info3)
        // Not equals for different types
        assertThat(info1.equals("a string")).isFalse()
        // Not equals for null
        assertThat(info1.equals(null)).isFalse()

        // Check with null fields
        assertThat(infoWithNulls1).isEqualTo(infoWithNulls2)
        assertThat(infoWithNulls1.hashCode()).isEqualTo(infoWithNulls2.hashCode())
        assertThat(info1).isNotEqualTo(infoWithNulls1)
    }

    @Test
    fun testToStringContainsFields() {
        val info = UpdateInfo(requiredVersion, lastVersionAvailable, requirements, installedVersion, notificationType)
        val str = info.toString()
        assertThat(str).contains("Installed version =")
        assertThat(str).contains("Required version =")
        assertThat(str).contains("Last version =")
        assertThat(str).contains("Requirements =")
    }
}
