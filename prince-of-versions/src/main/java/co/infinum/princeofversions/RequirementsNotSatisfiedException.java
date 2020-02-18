package co.infinum.princeofversions;

import java.util.Map;

/**
 * Exception for cases where there is no update with requirements satisfied
 */
class RequirementsNotSatisfiedException extends IllegalStateException {

    /**
     * Default metadata from root object in JSON file
     */
    private Map<String, Object> metadata;

    RequirementsNotSatisfiedException(Map<String, Object> metadata) {
        super();
        this.metadata = metadata;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return metadata.toString();
    }
}
