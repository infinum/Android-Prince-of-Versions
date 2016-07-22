package co.infinum.princeofversions.common;

public class VersionContext {

    private String currentVersion;
    private Version minimumVersion;
    private UpdateContext optionalUpdate;
    private boolean isCurrentLessThanMinimum;
    private boolean isCurrentLessThanOptional;

    public VersionContext(String currentVersion, Version minimumVersion, boolean isCurrentLessThanMinimum, UpdateContext optionalUpdate,
                          boolean isCurrentLessThanOptional) {
        this.currentVersion = currentVersion;
        this.minimumVersion = minimumVersion;
        this.optionalUpdate = optionalUpdate;
        this.isCurrentLessThanMinimum = isCurrentLessThanMinimum;
        this.isCurrentLessThanOptional = isCurrentLessThanOptional;
    }

    public VersionContext(Version minimumVersion, boolean isCurrentLessThanMinimum, UpdateContext optionalUpdate,
                          boolean isCurrentLessThanOptional) {
        this(null, minimumVersion, isCurrentLessThanMinimum, optionalUpdate, isCurrentLessThanOptional);
    }

    public VersionContext(String currentVersion, Version minimumVersion, boolean isCurrentLessThanMinimum) {
        this(currentVersion, minimumVersion, isCurrentLessThanMinimum, null, false);
    }

    public boolean hasOptionalUpdate() {
        return optionalUpdate != null;
    }

    public boolean isCurrentLessThanMinimum() {
        return isCurrentLessThanMinimum;
    }

    public boolean isCurrentLessThanOptional() {
        return isCurrentLessThanOptional;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public VersionContext setCurrentVersion(String v) {
        this.currentVersion = v;
        return this;
    }

    public Version getMinimumVersion() {
        return minimumVersion;
    }

    public UpdateContext getOptionalUpdate() {
        return optionalUpdate;
    }

    public static class Version {

        private String versionString;
        private String versionCode;

        public Version(String versionString, String versionCode) {
            this.versionString = versionString;
            this.versionCode = versionCode;
        }

        public Version(String versionString) {
            this(versionString, null);
        }

        public String getVersionString() {
            return versionString;
        }

        public String getVersionCode() {
            return versionCode;
        }

    }

    public static class UpdateContext {

        private Version version;
        private String notificationType;

        public UpdateContext(Version version, String notificationType) {
            this.version = version;
            this.notificationType = notificationType;
        }

        public Version getVersion() {
            return version;
        }

        public String getNotificationType() {
            return notificationType;
        }

    }

}
