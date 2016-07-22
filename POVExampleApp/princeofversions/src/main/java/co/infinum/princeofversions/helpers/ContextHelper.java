package co.infinum.princeofversions.helpers;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by stefano on 19/07/16.
 */
public class ContextHelper {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextHelper.context = context;
    }

    public static String getAppVersion(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }

    public static String getAppVersion() throws PackageManager.NameNotFoundException {
        return getAppVersion(getContext());
    }
}
