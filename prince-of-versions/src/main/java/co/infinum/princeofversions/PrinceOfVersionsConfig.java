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
    }

    public boolean hasOptional() {
        return optionalVersion != null && optionalMinSdk > 0 && optionalNotificationType != null;
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
            return new PrinceOfVersionsConfig(mandatoryVersion, mandatoryMinSdk, optionalVersion, optionalMinSdk, optionalNotificationType,
                    metadata != null ? metadata : new HashMap<String, String>());
        }
    }
}
