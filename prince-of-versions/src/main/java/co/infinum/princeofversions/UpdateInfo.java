package co.infinum.princeofversions;

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

    UpdateInfo(@Nullable Integer requiredVersion, @Nullable Integer lastVersionAvailable, Map<String, String> requirements, int installedVersion) {
        this.requiredVersion = requiredVersion;
        this.lastVersionAvailable = lastVersionAvailable;
        this.requirements = requirements;
        this.installedVersion = installedVersion;
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

    @Override
    public String toString() {
        return "Info{"
            + "Installed version =" + installedVersion
            + ", Required version ='" + requiredVersion + '\''
            + ", Last version ='" + lastVersionAvailable + '\''
            + ", Requirements =" + requirements
            + '}';
    }
}
