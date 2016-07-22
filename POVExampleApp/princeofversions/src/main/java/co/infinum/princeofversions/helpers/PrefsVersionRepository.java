package co.infinum.princeofversions.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.infinum.princeofversions.interfaces.VersionRepository;

public class PrefsVersionRepository implements VersionRepository {

    protected static String LAST_VERSION_KEY = "princeofversions.LastNotifiedVersion";

    private Context context;

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

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
