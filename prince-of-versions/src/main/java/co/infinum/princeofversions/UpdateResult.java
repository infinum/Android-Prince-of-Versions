package co.infinum.princeofversions;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * Represents the all information from update check.
 */
public class UpdateResult {

    /**
     * Merged metadata from JSON
     */
    private final Map<String, String> metadata;

    /**
     * Update configuration values used to check
     */
    private final UpdateInfo info;

    /**
     * Resolution of the update check
     */
    private final UpdateStatus status;

    /**
     * The biggest version it is possible to update to, or current version of the app if it isn't possible to update
     */
    private final int updateVersion;

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

    public int getUpdateVersion() {
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateResult)) {
            return false;
        }

        UpdateResult result = (UpdateResult) obj;

        if (this.status != result.status) {
            return false;
        }
        if (this.updateVersion != result.getUpdateVersion()) {
            return false;
        }
        if (!this.info.equals(result.getInfo())) {
            return false;
        }
        return metadata.equals(result.metadata);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + info.hashCode();
        result = 31 * result + metadata.hashCode();
        return result;
    }
}
