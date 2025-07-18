package co.infinum.princeofversions

/**
 * Provides application parameters such as version and SDK level.
 */
interface ApplicationConfiguration {
    /**
     * The application's version code.
     */
    val version: Int

    /**
     * The device's SDK version code.
     */
    val sdkVersionCode: Int
}
