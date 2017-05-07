package co.infinum.princeofversions.mocks;

import co.infinum.princeofversions.ApplicationConfiguration;

public class MockApplicationConfiguration implements ApplicationConfiguration {

    private String version;

    private int minSdk;

    public MockApplicationConfiguration(String version, int minSdk) {
        this.version = version;
        this.minSdk = minSdk;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public int minSdk() {
        return minSdk;
    }
}
