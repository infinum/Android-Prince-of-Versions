package co.infinum.princeofversions.mocks;

import co.infinum.princeofversions.Storage;

public class MockStorage implements Storage {

    private String value;

    public MockStorage() {
    }

    public MockStorage(String value) {
        this.value = value;
    }

    @Override
    public String lastNotifiedVersion(String defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public void rememberLastNotifiedVersion(String version) {
        this.value = version;
    }
}
