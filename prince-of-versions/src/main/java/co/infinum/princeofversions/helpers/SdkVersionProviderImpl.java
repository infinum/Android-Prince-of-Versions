package co.infinum.princeofversions.helpers;


import android.os.Build;

import co.infinum.princeofversions.interfaces.SdkVersionProvider;

public class SdkVersionProviderImpl implements SdkVersionProvider {

    @Override
    public int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }
}
