package co.infinum.princeofversions

/**
 * Represents selected update configuration object based on requirements
 */
data class UpdateInfo (
    val requiredVersion: Int?,
    val lastVersionAvailable: Int?,
    val requirements: Map<String, String>,
    val installedVersion: Int,
    val notificationFrequency: NotificationType
) {

    override fun toString(): String = "Info{" +
        "Installed version =" + installedVersion +
        ", Required version ='" + requiredVersion + '\'' +
        ", Last version ='" + lastVersionAvailable + '\'' +
        ", Requirements =" + requirements +
        '}'
}
