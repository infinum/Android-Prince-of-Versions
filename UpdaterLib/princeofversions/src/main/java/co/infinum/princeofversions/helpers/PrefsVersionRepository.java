package co.infinum.princeofversions.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.infinum.princeofversions.interfaces.VersionRepository;

/**
 * This class represents repository for persisting library data in SharedPreferences.
 */
public class PrefsVersionRepository implements VersionRepository {

    /**
     * Key that represents last notified version.
     */
    protected static String LAST_VERSION_KEY = "princeofversions.LastNotifiedVersion";

    /**
     * Current application context.
     */
    private Context context;

    /**
     * Creates a new repository from current application context.
     * @param context Current application context.
     */
    public PrefsVersionRepository(Context context) {
        this.context = context;
    }

    @Override
    public String getLastVersionName() {
        return getLastVersionName(null);
    }

    @Override
    public String getLastVersionName(String defaultValue) {
        return getPrefs().getString(LAST_VERSION_KEY, defaultValue);
    }

    @Override
    public void setLastVersionName(String version) {
        getPrefs().edit().putString(LAST_VERSION_KEY, version).commit();
    }

    /**
     * Utility method for getting shared preferences object.
     * @return SharedPreferences associated with current application.
     */
    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
