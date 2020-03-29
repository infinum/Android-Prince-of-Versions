package co.infinum.princeofversions;

import android.support.annotation.VisibleForTesting;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Represents selected update configuration object based on requirements
 */
public class UpdateInfo {

    /**
     * requiredVersion is required_version from selected object
     */
    @Nullable
    private final Integer requiredVersion;

    /**
     * lastVersionAvailable is last_version_available from selected object
     */
    @Nullable
    private final Integer lastVersionAvailable;

    /**
     * requirements is the map of requirements from selected object
     */
    private final Map<String, String> requirements;

    /**
     * installedVersion is versionCode of currently installed application
     */
    private final int installedVersion;

    /**
     * frequency of update reports
     */
    private final NotificationType notificationFrequency;

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public UpdateInfo(
            @Nullable Integer requiredVersion,
            @Nullable Integer lastVersionAvailable,
            Map<String, String> requirements,
            int installedVersion,
            NotificationType notificationFrequency
    ) {
        this.requiredVersion = requiredVersion;
        this.lastVersionAvailable = lastVersionAvailable;
        this.requirements = requirements;
        this.installedVersion = installedVersion;
        this.notificationFrequency = notificationFrequency;
    }

    public int getInstalledVersion() {
        return installedVersion;
    }

    @Nullable
    public Integer getLastVersionAvailable() {
        return lastVersionAvailable;
    }

    @Nullable
    public Integer getRequiredVersion() {
        return requiredVersion;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public NotificationType getNotificationFrequency() {
        return notificationFrequency;
    }

    @Override
    public String toString() {
        return "Info{"
                + "Installed version =" + installedVersion
                + ", Required version ='" + requiredVersion + '\''
                + ", Last version ='" + lastVersionAvailable + '\''
                + ", Requirements =" + requirements
                + '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateInfo)) {
            return false;
        }

        UpdateInfo result = (UpdateInfo) obj;

        if (this.installedVersion != result.getInstalledVersion()) {
            return false;
        }
        if (this.getRequiredVersion() != null) {
            if (!this.getRequiredVersion().equals(result.getRequiredVersion())) {
                return false;
            }
        } else {
            if (result.getRequiredVersion() != null) {
                return false;
            }
        }

        if (this.getLastVersionAvailable() != null) {
            if (!this.getLastVersionAvailable().equals(result.getLastVersionAvailable())) {
                return false;
            }
        } else {
            if (result.getLastVersionAvailable() != null) {
                return false;
            }
        }

        return this.requirements.equals(result.getRequirements());
    }

    @Override
    public int hashCode() {
        int result = requirements.hashCode();
        result = 31 * result + getInstalledVersion();
        result = 31 * result + (getLastVersionAvailable() != null ? getLastVersionAvailable() : 0);
        result = 31 * result + (getRequiredVersion() != null ? getRequiredVersion() : 0);
        return result;
    }
}
