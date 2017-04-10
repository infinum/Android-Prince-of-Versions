package co.infinum.princeofversions;

import java.util.HashMap;
import java.util.Map;

public class PrinceOfVersionsConfig {

    private String mandatoryVersion;

    private int mandatoryMinSdk;

    private String optionalVersion;

    private int optionalMinSdk;

    private NotificationType optionalNotificationType;

    private Map<String, String> metadata;

    public PrinceOfVersionsConfig(String mandatoryVersion, int mandatoryMinSdk, String optionalVersion, int optionalMinSdk,
            NotificationType optionalNotificationType, Map<String, String> metadata) {
        this.mandatoryVersion = mandatoryVersion;
        this.mandatoryMinSdk = mandatoryMinSdk;
        this.optionalVersion = optionalVersion;
        this.optionalMinSdk = optionalMinSdk;
        this.optionalNotificationType = optionalNotificationType;
        this.metadata = metadata;
        validate();
    }

    private void validate() {
        if (mandatoryVersion == null) {
            throw new IllegalArgumentException("Mandatory version must be present.");
        }
        if (mandatoryMinSdk <= 0) {
            throw new IllegalArgumentException("Mandatory minimum SDK version must be greater than 0.");
        }
        if (optionalNotificationType == null) {
            throw new IllegalArgumentException("Notification type cannot be null.");
        }
    }

    public boolean hasOptional() {
        return optionalVersion != null && optionalMinSdk > 0;
    }

    public String getMandatoryVersion() {
        return mandatoryVersion;
    }

    public int getMandatoryMinSdk() {
        return mandatoryMinSdk;
    }

    public String getOptionalVersion() {
        return optionalVersion;
    }

    public int getOptionalMinSdk() {
        return optionalMinSdk;
    }

    public NotificationType getOptionalNotificationType() {
        return optionalNotificationType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrinceOfVersionsConfig)) {
            return false;
        }

        PrinceOfVersionsConfig that = (PrinceOfVersionsConfig) o;

        if (getMandatoryMinSdk() != that.getMandatoryMinSdk()) {
            return false;
        }
        if (getOptionalMinSdk() != that.getOptionalMinSdk()) {
            return false;
        }
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
        return getMetadata() != null ? getMetadata().equals(that.getMetadata()) : that.getMetadata() == null;

    }

    @Override
    public int hashCode() {
        int result = getMandatoryVersion() != null ? getMandatoryVersion().hashCode() : 0;
        result = 31 * result + getMandatoryMinSdk();
        result = 31 * result + (getOptionalVersion() != null ? getOptionalVersion().hashCode() : 0);
        result = 31 * result + getOptionalMinSdk();
        result = 31 * result + (getOptionalNotificationType() != null ? getOptionalNotificationType().hashCode() : 0);
        result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrinceOfVersionsConfig{"
                + "mandatoryVersion='" + mandatoryVersion + '\''
                + ", mandatoryMinSdk=" + mandatoryMinSdk
                + ", optionalVersion='" + optionalVersion + '\''
                + ", optionalMinSdk=" + optionalMinSdk
                + ", optionalNotificationType=" + optionalNotificationType
                + ", metadata=" + metadata
                + '}';
    }

    public static class Builder {

        private String mandatoryVersion;

        private int mandatoryMinSdk = 1;

        private String optionalVersion;

        private int optionalMinSdk = 1;

        private NotificationType optionalNotificationType;

        private Map<String, String> metadata;

        public Builder() {
        }

        public String getMandatoryVersion() {
            return mandatoryVersion;
        }

        public Builder withMandatoryVersion(String mandatoryVersion) {
            this.mandatoryVersion = mandatoryVersion;
            return this;
        }

        public int getMandatoryMinSdk() {
            return mandatoryMinSdk;
        }

        public Builder withMandatoryMinSdk(int mandatoryMinSdk) {
            this.mandatoryMinSdk = mandatoryMinSdk;
            return this;
        }

        public String getOptionalVersion() {
            return optionalVersion;
        }

        public Builder withOptionalVersion(String optionalVersion) {
            this.optionalVersion = optionalVersion;
            return this;
        }

        public int getOptionalMinSdk() {
            return optionalMinSdk;
        }

        public Builder withOptionalMinSdk(int optionalMinSdk) {
            this.optionalMinSdk = optionalMinSdk;
            return this;
        }

        public NotificationType getOptionalNotificationType() {
            return optionalNotificationType;
        }

        public Builder withOptionalNotificationType(NotificationType optionalNotificationType) {
            this.optionalNotificationType = optionalNotificationType;
            return this;
        }

        public Map<String, String> getMetadata() {
            return metadata;
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
