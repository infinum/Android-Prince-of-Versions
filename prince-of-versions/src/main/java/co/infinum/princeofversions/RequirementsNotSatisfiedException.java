package co.infinum.princeofversions;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception for cases where there is no update with requirements satisfied
 */
public class RequirementsNotSatisfiedException extends IllegalStateException {

    /**
     * Default metadata from root object in JSON file
     */
    private final Map<String, String> metadata;

    RequirementsNotSatisfiedException(final Map<String, String> metadata) {
        this.metadata = new HashMap<>(metadata);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "RequirementsNotSatisfiedException{"
            + "metadata=" + metadata
            + '}';
    }
}
