package co.infinum.princeofversions.helpers;

import android.content.Context;

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
}
