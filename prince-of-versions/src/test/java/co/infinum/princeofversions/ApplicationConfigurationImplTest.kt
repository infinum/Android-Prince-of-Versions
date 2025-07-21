package co.infinum.princeofversions

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ApplicationConfigurationImplTest {

    @Test
    fun testVersionIsResolvedFromPackageManager() {
        val mockContext = mock(Context::class.java)
        val mockPackageManager = mock(PackageManager::class.java)
        val mockPackageInfo = PackageInfo()
        mockPackageInfo.versionCode = 123
        val packageName = "co.infinum.princeofversions.test"

        `when`(mockContext.packageName).thenReturn(packageName)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockPackageManager.getPackageInfo(packageName, 0)).thenReturn(mockPackageInfo)

        val appConfig = ApplicationConfigurationImpl(mockContext)

        assertThat(appConfig.version).isEqualTo(123)
    }

    @Test
    fun testSdkVersionIsResolvedFromBuild() {
        val mockContext = mock(Context::class.java)
        val mockPackageManager = mock(PackageManager::class.java)
        val mockPackageInfo = PackageInfo()
        val packageName = "co.infinum.princeofversions.test"

        `when`(mockContext.packageName).thenReturn(packageName)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockPackageManager.getPackageInfo(packageName, 0)).thenReturn(mockPackageInfo)

        val appConfig = ApplicationConfigurationImpl(mockContext)

        assertThat(appConfig.sdkVersionCode).isEqualTo(Build.VERSION.SDK_INT)
    }

    @Test
    fun testConstructorThrowsExceptionOnNameNotFound() {
        val mockContext = mock(Context::class.java)
        val mockPackageManager = mock(PackageManager::class.java)
        val packageName = "co.infinum.princeofversions.test"

        `when`(mockContext.packageName).thenReturn(packageName)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockPackageManager.getPackageInfo(packageName, 0)).thenThrow(PackageManager.NameNotFoundException())

        assertThatThrownBy { ApplicationConfigurationImpl(mockContext) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Could not find package name")
    }
}
