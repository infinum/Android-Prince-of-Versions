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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Result)) {
            return false;
        }

        Result result = (Result) o;

        if (getStatus() != result.getStatus()) {
            return false;
        }
        if (getVersion() != null ? !getVersion().equals(result.getVersion()) : result.getVersion() != null) {
            return false;
        }
        return getMetadata() != null ? getMetadata().equals(result.getMetadata()) : result.getMetadata() == null;

    }

    @Override
    public int hashCode() {
        int result = getStatus() != null ? getStatus().hashCode() : 0;
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{"
                + "status=" + status
                + ", version='" + version + '\''
                + ", metadata=" + metadata
                + '}';
    }
}
