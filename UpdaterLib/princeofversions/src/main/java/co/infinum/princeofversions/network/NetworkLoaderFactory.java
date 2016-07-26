package co.infinum.princeofversions.network;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.UpdateConfigLoader;

/**
 * Class creates specific network loader.
 */
public class NetworkLoaderFactory implements LoaderFactory {

    /**
     * Network resource locator.
     */
    private String url;

    /**
     * Creates a new factory based on provided resource locator.
     * @param url
     */
    public NetworkLoaderFactory(String url) {
        this.url = url;
    }

    @Override
    public UpdateConfigLoader newInstance() {
        return new NetworkLoader(url);
    }

}
