package co.infinum.princeofversions.loaders.factories;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.loaders.NetworkLoader;

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
     * @param url Resource locator.
     */
    public NetworkLoaderFactory(String url) {
        this.url = url;
    }

    @Override
    public UpdateConfigLoader newInstance() {
        return new NetworkLoader(url);
    }

}
