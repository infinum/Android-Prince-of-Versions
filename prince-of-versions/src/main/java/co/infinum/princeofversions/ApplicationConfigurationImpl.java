package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * This class provides application's version name and SDK version code.
 */
final class ApplicationConfigurationImpl implements ApplicationConfiguration {

    /**
     * Version code
     */
    private int version;

    /**
     * SDK version code
     */
    private int sdkVersionCode;

    /**
     * Creates application configuration for provided {@link Context}.
     *
     * @param context instance of application context from where is version name read.
     */
    ApplicationConfigurationImpl(Context context) {
        sdkVersionCode = Build.VERSION.SDK_INT;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Could not find package name", e);
        }
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
