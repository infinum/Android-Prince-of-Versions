package co.infinum.princeofversions.util;


import co.infinum.princeofversions.interfaces.SdkVersionProvider;

public class SdkVersionProviderMock implements SdkVersionProvider {

    private int sdkInt;

    public SdkVersionProviderMock(int sdkInt) {
        this.sdkInt = sdkInt;
    }

    @Override
    public int getSdkInt() {
        return sdkInt;
    }
}
