package co.infinum.princeofversions.network;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.UpdateConfigLoader;

public class NetworkLoaderFactory implements LoaderFactory {

    private String url;

    public NetworkLoaderFactory(String url) {
        this.url = url;
    }

    @Override
    public UpdateConfigLoader newInstance() {
        return new NetworkLoader(url);
    }

}
