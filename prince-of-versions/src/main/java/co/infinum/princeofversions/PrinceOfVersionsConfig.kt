package co.infinum.princeofversions

import java.util.HashMap

/**
 * This class holds loaded data from a configuration resource.
 *
 */
@ConsistentCopyVisibility
data class PrinceOfVersionsConfig private constructor(
    val mandatoryVersion: Int?,
    val optionalVersion: Int?,
    val optionalNotificationType: NotificationType,
    val metadata: Map<String, String?>,
    val requirements: Map<String, String>
) {

    /**
     * Builds a new [PrinceOfVersionsConfig].
     * All methods are optional.
     */
    class Builder {
        private var mandatoryVersion: Int? = null
        private var optionalVersion: Int? = null
        private var optionalNotificationType: NotificationType? = null
        private var requirements: Map<String, String>? = null
        private val metadata: MutableMap<String, String?> = HashMap()

        /**
         * Sets a new mandatory version.
         *
         * @param mandatoryVersion The mandatory version number.
         * @return this builder.
         */
        fun withMandatoryVersion(mandatoryVersion: Int?) = apply {
            this.mandatoryVersion = mandatoryVersion
        }

        /**
         * Sets a new optional version.
         *
         * @param optionalVersion The optional version number.
         * @return this builder.
         */
        fun withOptionalVersion(optionalVersion: Int?) = apply {
            this.optionalVersion = optionalVersion
        }

        /**
         * Sets a new notification type for an optional update.
         *
         * @param optionalNotificationType The notification type.
         * @return this builder.
         */
        fun withOptionalNotificationType(optionalNotificationType: NotificationType) = apply {
            this.optionalNotificationType = optionalNotificationType
        }

        /**
         * Sets new metadata about the update.
         *
         * @param metadata A map of metadata.
         * @return this builder.
         */
        fun withMetadata(metadata: Map<String, String?>) = apply {
            this.metadata.putAll(metadata)
        }

        /**
         * Sets new requirements for the update.
         *
         * @param requirements A map of requirements.
         * @return this builder.
         */
        fun withRequirements(requirements: Map<String, String>) = apply {
            this.requirements = requirements
        }

        /**
         * Creates the [PrinceOfVersionsConfig] instance using the configured values.
         *
         * @return A new [PrinceOfVersionsConfig] instance.
         */
        fun build(): PrinceOfVersionsConfig {
            return PrinceOfVersionsConfig(
                mandatoryVersion = mandatoryVersion,
                optionalVersion = optionalVersion,
                optionalNotificationType = optionalNotificationType ?: NotificationType.ONCE,
                metadata = metadata,
                requirements = requirements ?: emptyMap()
            )
        }
    }
}
