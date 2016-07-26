package co.infinum.princeofversions;

import java.io.IOException;

/**
 * Represents abstract loader for loading update configuration resource.
 */
public interface UpdateConfigLoader {

    /**
     * Method loads resource as string representation.
     * @return String representing the resource.
     * @throws IOException if error occurred while reading.
     * @throws InterruptedException if loading is cancelled.
     */
    String load() throws IOException, InterruptedException;

    /**
     * Cancelling loading of resource.
     */
    void cancel();

    /**
     * Validate current loader's state.
     * @throws LoaderValidationException if loader is not in valid state.
     */
    void validate() throws LoaderValidationException;

}
