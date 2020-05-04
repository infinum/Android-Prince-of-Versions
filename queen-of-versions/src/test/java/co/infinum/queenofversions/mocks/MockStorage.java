package co.infinum.queenofversions.mocks;

import javax.annotation.Nullable;

import co.infinum.princeofversions.Storage;

public class MockStorage implements Storage {

    private Integer value;

    public MockStorage() {
    }

    public MockStorage(int value) {
        this.value = value;
    }

    @Nullable
    @Override
    public Integer lastNotifiedVersion(@Nullable Integer defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public void rememberLastNotifiedVersion(@Nullable Integer version) {
        this.value = version;
    }
}
