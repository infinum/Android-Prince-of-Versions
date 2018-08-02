package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * This class provides application's version name and minimum SDK version.
 */
public class ApplicationConfigurationImpl implements ApplicationConfiguration {

    /**
     * Version name
     */
    private String version;

    /**
     * Minimum SDK version
     */
    private int minSdk;

    /**
     * Creates application configuration for provided {@link Context}.
     *
     * @param context instance of application context from where is version name read.
     */
    public ApplicationConfigurationImpl(Context context) {
        minSdk = Build.VERSION.SDK_INT;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Could not find package name", e);
        }
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
