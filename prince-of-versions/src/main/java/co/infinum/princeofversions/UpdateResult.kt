package co.infinum.princeofversions

/**
 * Represents all the information from the update check.
 *
 * @property metadata Merged metadata from JSON.
 * @property info Update configuration values used to check.
 * @property status Resolution of the update check.
 * @property updateVersion The biggest version it is possible to update to, or current version of the app if no update is possible.
 */
data class UpdateResult(
    val info: UpdateInfo,
    val metadata: Map<String, String?>,
    val status: UpdateStatus,
    val updateVersion: Int
) {

    override fun toString(): String = "UpdateResult{" +
        "metadata=" + metadata +
        ", info=" + info +
        ", status=" + status +
        ", updateVersion=" + updateVersion +
        '}'
}
