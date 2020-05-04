package co.infinum.princeofversions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * This class holds loaded data from config resource.
 */
public final class PrinceOfVersionsConfig {

    /**
     * Mandatory version
     */
    @Nullable
    private final Integer mandatoryVersion;

    /**
     * Optional version
     */
    @Nullable
    private final Integer optionalVersion;

    /**
     * Notification type
     */
    private final NotificationType optionalNotificationType;

    /**
     * Metadata of the update configuration
     */
    private final Map<String, String> metadata;

    /**
     * Requirements from the ongoing update
     */
    private final Map<String, String> requirements;

    PrinceOfVersionsConfig(
        @Nullable Integer mandatoryVersion,
        @Nullable Integer optionalVersion,
        NotificationType optionalNotificationType,
        Map<String, String> metadata,
        Map<String, String> requirements) {

        this.mandatoryVersion = mandatoryVersion;
        this.optionalVersion = optionalVersion;
        this.optionalNotificationType = optionalNotificationType;
        this.metadata = metadata;
        this.requirements = requirements;
    }

    @Nullable
    Integer getMandatoryVersion() {
        return mandatoryVersion;
    }

    @Nullable
    Integer getOptionalVersion() {
        return optionalVersion;
    }

    NotificationType getOptionalNotificationType() {
        return optionalNotificationType;
    }

    Map<String, String> getMetadata() {
        return metadata;
    }

    Map<String, String> getRequirements() {
        return requirements;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrinceOfVersionsConfig)) {
            return false;
        }

        final PrinceOfVersionsConfig that = (PrinceOfVersionsConfig) o;

        if (getMandatoryVersion() != null ? !getMandatoryVersion().equals(that.getMandatoryVersion())
            : that.getMandatoryVersion() != null) {
            return false;
        }
        if (getOptionalVersion() != null ? !getOptionalVersion().equals(that.getOptionalVersion()) : that.getOptionalVersion() != null) {
            return false;
        }
        if (getOptionalNotificationType() != that.getOptionalNotificationType()) {
            return false;
        }
        return getMetadata().equals(that.getMetadata());
    }

    @Override
    public int hashCode() {
        int result = getMandatoryVersion() != null ? getMandatoryVersion().hashCode() : 0;
        result = 31 * result + (getOptionalVersion() != null ? getOptionalVersion().hashCode() : 0);
        result = 31 * result + getOptionalNotificationType().hashCode();
        result = 31 * result + getMetadata().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PrinceOfVersionsConfig{"
            + "mandatoryVersion=" + mandatoryVersion
            + ", optionalVersion=" + optionalVersion
            + ", optionalNotificationType=" + optionalNotificationType
            + ", metadata=" + metadata
            + '}';
    }

    /**
     * Builds a new {@link PrinceOfVersionsConfig}.
     * All methods are optional.
     */
    public static class Builder {

        @Nullable
        private Integer mandatoryVersion;

        @Nullable
        private Integer optionalVersion;

        @Nullable
        private NotificationType optionalNotificationType;

        @Nullable
        private Map<String, String> requirements;

        private Map<String, String> metadata;

        public Builder() {
            this.metadata = new HashMap<>();
        }

        /**
         * Set a new mandatory version string.
         *
         * @param mandatoryVersion Mandatory version name
         * @return this builder
         */
        public Builder withMandatoryVersion(@Nullable Integer mandatoryVersion) {
            this.mandatoryVersion = mandatoryVersion;
            return this;
        }

        /**
         * Set a new optional version string.
         *
         * @param optionalVersion Optional version name
         * @return this builder
         */
        public Builder withOptionalVersion(@Nullable Integer optionalVersion) {
            this.optionalVersion = optionalVersion;
            return this;
        }

        /**
         * Set a new notification type of optional update.
         *
         * @param optionalNotificationType Notification type
         * @return this builder
         */
        public Builder withOptionalNotificationType(NotificationType optionalNotificationType) {
            this.optionalNotificationType = optionalNotificationType;
            return this;
        }

        /**
         * Set new metadata about the update.
         *
         * @param metadata String to string map
         * @return this builder
         */
        public Builder withMetadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        /**
         * Set new requirements about the update
         *
         * @param requirements String to string map
         * @return this builder
         */
        public Builder withRequirements(Map<String, String> requirements) {
            this.requirements = requirements;
            return this;
        }

        /**
         * Create the {@link PrinceOfVersionsConfig} instance using the configured values.
         *
         * @return A new {@link PrinceOfVersionsConfig} instance
         */
        public PrinceOfVersionsConfig build() {
            return new PrinceOfVersionsConfig(
                mandatoryVersion,
                optionalVersion,
                optionalNotificationType != null ? optionalNotificationType : NotificationType.ONCE,
                metadata,
                requirements != null ? requirements : new HashMap<String, String>());
        }
    }
}
