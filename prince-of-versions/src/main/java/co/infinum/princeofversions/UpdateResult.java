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
    private UpdateInfo info;
    private UpdateStatus status;

    public UpdateResult(UpdateInfo info, Map<String, String> metadata, UpdateStatus status) {
        this.info = info;
        this.metadata = metadata;
        this.status = status;
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

    @Override
    public String toString() {
        return "Update Result {"
            + " Info ='" + info + '\''
            + ", Status ='" + status + '\''
            + ", Metadata =" + metadata
            + '}';
    }
}
