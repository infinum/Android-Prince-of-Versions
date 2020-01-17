package co.infinum.princeofversions;

/**
 * Class provides application's parameters:
 * <ul>
 * <li>version name</li>
 * <li>device SDK version code</li>
 * </ul>
 */
public interface ApplicationConfiguration {

    /**
     * Method provides application's version code
     *
     * @return application version code
     */
    int version();

    /**
     * Method provides device's SDK version code
     *
     * @return SDK version code
     */
    int sdkVersionCode();

}
