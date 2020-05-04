package co.infinum.princeofversions.mocks;

import co.infinum.princeofversions.ApplicationConfiguration;

public class MockApplicationConfiguration implements ApplicationConfiguration {

    private int version;

    private int sdkVersionCode;

    public MockApplicationConfiguration(int version, int sdkVersionCode) {
        this.version = version;
        this.sdkVersionCode = sdkVersionCode;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public int sdkVersionCode() {
        return sdkVersionCode;
    }
}
