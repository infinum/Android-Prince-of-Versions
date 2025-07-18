package co.infinum.princeofversions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * This class provides the application's version and the device's SDK version code.
 *
 * @param context The application context used to retrieve package information.
 */
internal class ApplicationConfigurationImpl(context: Context) : ApplicationConfiguration {

    /**
     * The application's version code, retrieved from the package manager.
     * @throws IllegalStateException if the application's package name cannot be found.
     */
    override val version: Int = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        throw IllegalStateException("Could not find package name", e)
    }

    /**
     * The SDK version of the Android OS on which the app is running.
     */
    override val sdkVersionCode: Int = Build.VERSION.SDK_INT
}
