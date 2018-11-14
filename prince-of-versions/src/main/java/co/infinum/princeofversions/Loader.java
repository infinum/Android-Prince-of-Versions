package co.infinum.princeofversions;

/**
 * This class loads update resource.
 */
public interface Loader {

    /**
     * Loads update resource into {@link String}.
     *
     * @return Loaded text.
     * @throws Throwable if error happens during load.
     */
    String load() throws Throwable;

}
