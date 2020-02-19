package co.infinum.princeofversions;

import java.util.Map;

/**
 * Exception for cases where there is no update with requirements satisfied
 */
class RequirementsNotSatisfiedException extends IllegalStateException {

    /**
     * Default metadata from root object in JSON file
     */
    private Map<String, String> metadata;

    RequirementsNotSatisfiedException(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return metadata.toString();
    }
}
