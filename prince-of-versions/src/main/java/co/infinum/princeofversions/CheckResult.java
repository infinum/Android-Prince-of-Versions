package co.infinum.princeofversions;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Intermediate result of update check. This result contains following data:
 * <ul>
 * <li>Update status (one of MANDATORY, OPTIONAL or NO_UPDATE)</li>
 * <li>Update version</li>
 * <li>Notification type (ONCE or ALWAYS)</li>
 * <li>Metadata</li>
 * </ul>
 */
final class CheckResult {

    private UpdateStatus status;

    private Integer updateVersion;

    @Nullable
    private NotificationType notificationType;

    private Map<String, String> metadata;

    private CheckResult(UpdateStatus status, Integer updateVersion, @Nullable NotificationType notificationType,
        Map<String, String> metadata) {
        this.status = status;
        this.updateVersion = updateVersion;
        this.notificationType = notificationType;
        this.metadata = metadata;
    }

    static CheckResult mandatoryUpdate(Integer version, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.MANDATORY, version, null, metadata);
    }

    static CheckResult optionalUpdate(Integer version, NotificationType notificationType, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.OPTIONAL, version, notificationType, metadata);
    }

    static CheckResult noUpdate(Integer version, Map<String, String> metadata) {
        return new CheckResult(UpdateStatus.NO_UPDATE, version, null, metadata);
    }

    boolean hasUpdate() {
        return UpdateStatus.MANDATORY.equals(status) || UpdateStatus.OPTIONAL.equals(status);
    }

    int getUpdateVersion() {
        return updateVersion;
    }

    boolean isOptional() {
        if (hasUpdate()) {
            return UpdateStatus.OPTIONAL.equals(status);
        } else {
            throw new UnsupportedOperationException("There is no update available.");
        }
    }

    @Nullable
    NotificationType getNotificationType() {
        if (isOptional()) {
            return notificationType;
        } else {
            throw new UnsupportedOperationException("There is no optional update available.");
        }
    }

    UpdateStatus status() {
        return status;
    }

    Map<String, String> metadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckResult)) {
            return false;
        }

        CheckResult result = (CheckResult) o;

        if (status != result.status) {
            return false;
        }
        if (getUpdateVersion() != (result.getUpdateVersion())) {
            return false;
        }
        if (notificationType != result.notificationType) {
            return false;
        }
        return metadata.equals(result.metadata);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (getUpdateVersion());
        result = 31 * result + (getNotificationType() != null ? getNotificationType().hashCode() : 0);
        result = 31 * result + (metadata.hashCode());
        return result;
    }
}
