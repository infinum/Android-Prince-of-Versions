package co.infinum.princeofversions.helpers;

import android.content.Context;
import android.content.pm.PackageManager;

import co.infinum.princeofversions.common.VersionContext;

/**
 * Created by stefano on 19/07/16.
 */
public class ContextHelper {

    private ContextHelper() {
    }

    public static VersionContext.Version getAppVersion(Context context) throws PackageManager.NameNotFoundException {
        return new VersionContext.Version(
                context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
    }
}
