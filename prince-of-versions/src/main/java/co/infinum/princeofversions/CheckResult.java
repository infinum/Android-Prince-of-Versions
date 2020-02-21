package co.infinum.princeofversions;

import android.support.annotation.NonNull;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Intermediate result of update check. This result contains following data:
 * <ul>
 * <li>Update status (one of REQUIRED_UPDATE_NEEDED, NEW_UPDATE_AVAILABLE or NO_UPDATE_AVAILABLE)</li>
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

    private UpdateInfo info;

    private CheckResult(UpdateStatus status, Integer updateVersion, @Nullable NotificationType notificationType,
        Map<String, String> metadata, UpdateInfo updateInfo) {
        this.status = status;
        this.updateVersion = updateVersion;
        this.notificationType = notificationType;
        this.metadata = metadata;
        this.info = updateInfo;
    }

    static CheckResult mandatoryUpdate(Integer version, Map<String, String> metadata, UpdateInfo updateInfo) {
        return new CheckResult(UpdateStatus.REQUIRED_UPDATE_NEEDED, version, null, metadata, updateInfo);
    }

    static CheckResult optionalUpdate(Integer version, NotificationType notificationType, Map<String, String> metadata,
        UpdateInfo updateInfo) {
        return new CheckResult(UpdateStatus.NEW_UPDATE_AVAILABLE, version, notificationType, metadata, updateInfo);
    }

    static CheckResult noUpdate(Integer version, Map<String, String> metadata, UpdateInfo updateInfo) {
        return new CheckResult(UpdateStatus.NO_UPDATE_AVAILABLE, version, null, metadata, updateInfo);
    }

    boolean hasUpdate() {
        return UpdateStatus.REQUIRED_UPDATE_NEEDED.equals(status) || UpdateStatus.NEW_UPDATE_AVAILABLE.equals(status);
    }

    int getUpdateVersion() {
        return updateVersion;
    }

    boolean isOptional() {
        if (hasUpdate()) {
            return UpdateStatus.NEW_UPDATE_AVAILABLE.equals(status);
        } else {
            throw new UnsupportedOperationException("There is no update available.");
        }
    }

    public UpdateInfo getInfo() {
        return info;
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

    @Override
    public String toString() {
        return this.status + " " + this.info + " " + this.metadata + " " + this.notificationType;
    }
}
