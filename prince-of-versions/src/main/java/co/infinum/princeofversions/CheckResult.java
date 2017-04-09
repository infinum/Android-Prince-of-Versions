package co.infinum.princeofversions;

import java.util.Map;

public class CheckResult {

    private UpdateStatus status;

    private String updateVersion;

    private NotificationType notificationType;

    private Map<String, String> metadata;

    private CheckResult(UpdateStatus status, String updateVersion, NotificationType notificationType, Map<String, String> metadata) {
        this.status = status;
        this.updateVersion = updateVersion;
        this.notificationType = notificationType;
        this.metadata = metadata;
    }

    public static CheckResult mandatoryUpdate(String version, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.MANDATORY, version, null, metadata);
    }

    public static CheckResult optionalUpdate(String version, NotificationType notificationType, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.OPTIONAL, version, notificationType, metadata);
    }

    public static CheckResult noUpdate(String version, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.NO_UPDATE, version, null, metadata);
    }

    public boolean hasUpdate() {
        return UpdateStatus.MANDATORY.equals(status) || UpdateStatus.OPTIONAL.equals(status);
    }

    public String getUpdateVersion() {
        if (hasUpdate()) {
            return updateVersion;
        } else {
            throw new UnsupportedOperationException("There is no update available.");
        }
    }

    public boolean isOptional() {
        if (hasUpdate()) {
            return UpdateStatus.OPTIONAL.equals(status);
        } else {
            throw new UnsupportedOperationException("There is no update available.");
        }
    }

    public NotificationType getNotificationType() {
        if (isOptional()) {
            return notificationType;
        } else {
            throw new UnsupportedOperationException("There is no optional update available.");
        }
    }

    public UpdateStatus status() {
        return status;
    }

    public Map<String, String> metadata() {
        return metadata;
    }
}
