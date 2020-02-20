package co.infinum.princeofversions;

import java.util.Map;

/**
 * Represents the all information from update check.
 */
public class UpdateResult {

    /**
     * Merged metadata from JSON
     */
    private Map<String, String> metadata;

    /**
     * Update configuration values used to check
     */
    private UpdateInfo info;

    /**
     * Resolution of the update check
     */
    private UpdateStatus status;

    /**
     * The biggest version it is possible to update to, or current version of the app if it isn't possible to update
     */
    private int updateVersion;

    public UpdateResult(UpdateInfo info, Map<String, String> metadata, UpdateStatus status, int updateVersion) {
        this.info = info;
        this.metadata = metadata;
        this.status = status;
        this.updateVersion = updateVersion;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public UpdateInfo getInfo() {
        return info;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public int updateVersion() {
        return updateVersion;
    }

    @Override
    public String toString() {
        return "UpdateResult{"
            + "metadata=" + metadata
            + ", info=" + info
            + ", status=" + status
            + ", updateVersion=" + updateVersion
            + '}';
    }
}
