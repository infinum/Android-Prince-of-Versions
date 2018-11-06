package co.infinum.princeofversions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class holds loaded data from config resource.
 */
public final class PrinceOfVersionsConfig {

    /**
     * Mandatory version
     */
    @Nullable
    private final Version mandatoryVersion;

    /**
     * Optional version
     */
    @Nullable
    private final Version optionalVersion;

    /**
     * Notification type
     */
    private final NotificationType optionalNotificationType;

    /**
     * Metadata of the update configuration
     */
    private final Map<String, String> metadata;

    PrinceOfVersionsConfig(
        @Nullable String mandatoryVersion,
        int mandatoryMinSdk,
        @Nullable String optionalVersion,
        int optionalMinSdk,
        @Nonnull NotificationType optionalNotificationType,
        @Nonnull Map<String, String> metadata) {

        this.mandatoryVersion = (mandatoryVersion != null && mandatoryMinSdk > 0) ? new Version(mandatoryVersion, mandatoryMinSdk) : null;
        this.optionalVersion = (optionalVersion != null && optionalMinSdk > 0) ? new Version(optionalVersion, optionalMinSdk) : null;
        this.optionalNotificationType = optionalNotificationType;
        this.metadata = metadata;
    }

    @Nullable
    Version getMandatoryVersion() {
        return mandatoryVersion;
    }

    @Nullable
    Version getOptionalVersion() {
        return optionalVersion;
    }

    NotificationType getOptionalNotificationType() {
        return optionalNotificationType;
    }

    Map<String, String> getMetadata() {
        return metadata;
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

        if (getMandatoryVersion() != null ? !getMandatoryVersion().equals(that.getMandatoryVersion()) :
            that.getMandatoryVersion() != null) {
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

    static class Version {

        /**
         * Version string
         */
        private final String version;

        /**
         * MinSdk for update
         */
        private final int minSdk;

        Version(final String version, final int minSdk) {
            this.version = version;
            this.minSdk = minSdk;
        }

        String getVersion() {
            return version;
        }

        int getMinSdk() {
            return minSdk;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Version)) {
                return false;
            }

            final Version version1 = (Version) o;

            if (getMinSdk() != version1.getMinSdk()) {
                return false;
            }
            return getVersion().equals(version1.getVersion());
        }

        @Override
        public int hashCode() {
            int result = getVersion().hashCode();
            result = 31 * result + getMinSdk();
            return result;
        }

        @Override
        public String toString() {
            return "Version{"
                + "version='" + version + '\''
                + ", minSdk=" + minSdk
                + '}';
        }
    }

    public static class Builder {

        @Nullable
        private String mandatoryVersion;

        private int mandatoryMinSdk = 1;

        @Nullable
        private String optionalVersion;

        private int optionalMinSdk = 1;

        @Nullable
        private NotificationType optionalNotificationType;

        @Nullable
        private Map<String, String> metadata;

        public Builder() {
        }

        public Builder withMandatoryVersion(String mandatoryVersion) {
            this.mandatoryVersion = mandatoryVersion;
            return this;
        }

        public Builder withMandatoryMinSdk(int mandatoryMinSdk) {
            this.mandatoryMinSdk = mandatoryMinSdk;
            return this;
        }

        public Builder withOptionalVersion(String optionalVersion) {
            this.optionalVersion = optionalVersion;
            return this;
        }

        public Builder withOptionalMinSdk(int optionalMinSdk) {
            this.optionalMinSdk = optionalMinSdk;
            return this;
        }

        public Builder withOptionalNotificationType(NotificationType optionalNotificationType) {
            this.optionalNotificationType = optionalNotificationType;
            return this;
        }

        public Builder withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public PrinceOfVersionsConfig build() {
            return new PrinceOfVersionsConfig(
                mandatoryVersion,
                mandatoryMinSdk,
                optionalVersion,
                optionalMinSdk,
                optionalNotificationType != null ? optionalNotificationType : NotificationType.ONCE,
                metadata != null ? metadata : new HashMap<String, String>());
        }
    }
}
