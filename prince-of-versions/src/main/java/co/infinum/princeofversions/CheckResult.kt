package co.infinum.princeofversions

/**
 * Intermediate result of update check.
 *
 * This result contains:
 * - Update status (REQUIRED_UPDATE_NEEDED, NEW_UPDATE_AVAILABLE, or NO_UPDATE_AVAILABLE)
 * - Update version
 * - Notification type (ONCE or ALWAYS, or null)
 * - Metadata
 * - Info
 */
class CheckResult private constructor(
    val status: UpdateStatus,
    val updateVersion: Int,
    val notificationType: NotificationType?,
    val metadata: Map<String, String>,
    val info: UpdateInfo
) {

    companion object {

        fun mandatoryUpdate(
            version: Int,
            metadata: Map<String, String>,
            updateInfo: UpdateInfo
        ): CheckResult = CheckResult(
            status = UpdateStatus.REQUIRED_UPDATE_NEEDED,
            updateVersion = version,
            notificationType = null,
            metadata = metadata,
            info = updateInfo
        )

        fun optionalUpdate(
            version: Int,
            notificationType: NotificationType,
            metadata: Map<String, String>,
            updateInfo: UpdateInfo
        ): CheckResult = CheckResult(
            status = UpdateStatus.NEW_UPDATE_AVAILABLE,
            updateVersion = version,
            notificationType = notificationType,
            metadata = metadata,
            info = updateInfo
        )

        fun noUpdate(
            version: Int,
            metadata: Map<String, String>,
            updateInfo: UpdateInfo
        ): CheckResult = CheckResult(
            status = UpdateStatus.NO_UPDATE_AVAILABLE,
            updateVersion = version,
            notificationType = null,
            metadata = metadata,
            info = updateInfo
        )
    }

    fun hasUpdate(): Boolean =
        status == UpdateStatus.REQUIRED_UPDATE_NEEDED || status == UpdateStatus.NEW_UPDATE_AVAILABLE

    fun isOptional(): Boolean =
        if (hasUpdate()) status == UpdateStatus.NEW_UPDATE_AVAILABLE
        else throw UnsupportedOperationException("There is no update available.")

    fun safeNotificationType(): NotificationType? =
        if (isOptional()) notificationType
        else throw UnsupportedOperationException("There is no optional update available.")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CheckResult) return false

        return listOf(status, updateVersion, notificationType, metadata, info) ==
            listOf(other.status, other.updateVersion, other.notificationType, other.metadata, other.info)
    }


    override fun toString(): String {
        return "$status $info $metadata $notificationType"
    }

    override fun hashCode(): Int {
        var result = updateVersion
        result = 31 * result + status.hashCode()
        result = 31 * result + (notificationType?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        result = 31 * result + info.hashCode()
        return result
    }
}
