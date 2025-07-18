package co.infinum.princeofversions

import java.util.HashMap

/**
 * An exception thrown when there is no available update that satisfies the specified requirements.
 *
 * @param sourceMetadata The default metadata from the root object in the configuration file.
 * A defensive copy is made to ensure immutability.
 */
class RequirementsNotSatisfiedException(sourceMetadata: Map<String, String>) : IllegalStateException() {

    /**
     * A copy of the metadata from the update configuration.
     */
    val metadata: Map<String, String> = HashMap(sourceMetadata)

    override fun toString(): String {
        return "RequirementsNotSatisfiedException(metadata=$metadata)"
    }
}
