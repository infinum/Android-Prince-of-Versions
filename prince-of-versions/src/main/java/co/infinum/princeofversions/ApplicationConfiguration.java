package co.infinum.princeofversions;

/**
 * Class provides application's parameters:
 * <ul>
 * <li>version name</li>
 * <li>minimum SDK version</li>
 * </ul>
 */
public interface ApplicationConfiguration {

    /**
     * Method provides application's version name
     *
     * @return application version name
     */
    String version();

    /**
     * Method provides application's minimum SDK version
     *
     * @return minimum SDK version
     */
    int minSdk();

}
