package co.infinum.princeofversions;

import java.util.Map;

/**
 * This class holds result data of update check. It is possible to fetch all important information about the update:
 * 1. status of an update
 * 2. version to which application would be updated
 * 3. metadata of update check
 *
 * If status is MANDATORY or OPTIONAL then update exists and version contains data about version of an app provided in update.
 * On the other hand, if status is NO_UPDATE then there is no new update. In that case version contains application's current version.
 */
public final class Result {

    /**
     * Holds information about update status.
     */
    private UpdateStatus status;

    /**
     * Holds version string of update or if no update, application's version
     */
    private int version;

    /**
     * Metadata about update
     */
    private Map<String, Object> metadata;

    Result(UpdateStatus status, int version, Map<String, Object> metadata) {
        this.status = status;
        this.version = version;
        this.metadata = metadata;
    }

    /**
     * Returns status of an update.
     *
     * @return Update's status.
     */
    public UpdateStatus getStatus() {
        return status;
    }

    /**
     * Returns version of new update if there is an update, or application's current version if there is no update.
     *
     * @return version of update
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns update's metadata.
     *
     * @return metadata of update
     */
    public Map<String, Object> getMetadata() {
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
        if (getVersion() != result.getVersion()) {
            return false;
        }
        return getMetadata().equals(result.getMetadata());

    }

    @Override
    public int hashCode() {
        int result = getStatus().hashCode();
        result = 31 * result + getVersion();
        result = 31 * result + getMetadata().hashCode();
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
