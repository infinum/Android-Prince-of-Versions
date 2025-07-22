package co.infinum.princeofversions

import android.os.Build
import androidx.annotation.VisibleForTesting

/**
 * The default [RequirementChecker] implementation.
 *
 * @param sdkVersionProvider A function that provides the current device's SDK version.
 * Defaults to returning `Build.VERSION.SDK_INT`.
 */
internal class PrinceOfVersionsDefaultRequirementsChecker @VisibleForTesting internal constructor(
    private val sdkVersionProvider: () -> Int
) : RequirementChecker {

    /**
     * Creates a requirement checker that uses the device's current SDK version.
     */
    constructor() : this({ Build.VERSION.SDK_INT })

    companion object {
        const val KEY = "required_os_version"
    }

    /**
     * Checks if the device's OS version meets the required minimum version.
     *
     * @param value The minimum required OS version as a String.
     * @return true if the device's OS version is greater than or equal to the required version.
     */
    override fun checkRequirements(value: String): Boolean {
        val minSdk = value.toInt()
        return minSdk <= sdkVersionProvider()
    }
}
