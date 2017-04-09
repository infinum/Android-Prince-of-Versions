package co.infinum.princeofversions;

import java.util.Map;

public class Result {

    private UpdateStatus status;

    private String version;

    private Map<String, String> metadata;

    public Result(UpdateStatus status, String version, Map<String, String> metadata) {
        this.status = status;
        this.version = version;
        this.metadata = metadata;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
