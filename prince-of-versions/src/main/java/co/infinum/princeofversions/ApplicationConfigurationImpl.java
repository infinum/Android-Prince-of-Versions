package co.infinum.princeofversions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class ApplicationConfigurationImpl implements ApplicationConfiguration {

    private String version;

    private int minSdk;

    public ApplicationConfigurationImpl(Context context) {
        minSdk = Build.VERSION.SDK_INT;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new Exceptions.PrinceOfVersionsException("Could not find package name", e);
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
