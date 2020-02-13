package co.infinum.princeofversions.mocks;

import co.infinum.princeofversions.ApplicationConfiguration;

public class MockApplicationConfiguration implements ApplicationConfiguration {

    private String version;

    private int sdkVersionCode;

    public MockApplicationConfiguration(String version, int sdkVersionCode) {
        this.version = version;
        this.sdkVersionCode = sdkVersionCode;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public int sdkVersionCode() {
        return sdkVersionCode;
    }
}
