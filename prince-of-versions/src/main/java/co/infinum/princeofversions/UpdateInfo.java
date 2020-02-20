package co.infinum.princeofversions;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Represents selected update configuration object based on requirements
 */
public class UpdateInfo {

    /**
     * @param requiredVersion is required_version from selected object
     */
    @Nullable
    private int requiredVersion;

    /**
     * @param lastVersionAvailable is last_version_available from selected object
     */
    @Nullable
    private int lastVersionAvailable;

    /**
     * @param requirements is the map of requirements from selected object
     */
    private Map<String, String> requirements;

    /**
     * @param installedVersion is versionCode of currently installed application
     */
    private int installedVersion;

    public UpdateInfo(int requiredVersion, int lastVersionAvailable, Map<String, String> requirements, int installedVersion) {
        this.requiredVersion = requiredVersion;
        this.lastVersionAvailable = lastVersionAvailable;
        this.requirements = requirements;
        this.installedVersion = installedVersion;
    }

    public int getInstalledVersion() {
        return installedVersion;
    }

    public int getLastVersionAvailable() {
        return lastVersionAvailable;
    }

    public int getRequiredVersion() {
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
