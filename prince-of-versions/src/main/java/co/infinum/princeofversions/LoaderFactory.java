package co.infinum.princeofversions;

/**
 * Represents factory for creating new instance of loader for loading resource.
 */
public interface LoaderFactory {

    /**
     * Method provides <b>new</b> instance of specific UpdateConfigLoader loader.
     *
     * @return <b>New</b> instance of specific loader.
     */
    UpdateConfigLoader newInstance();

}
