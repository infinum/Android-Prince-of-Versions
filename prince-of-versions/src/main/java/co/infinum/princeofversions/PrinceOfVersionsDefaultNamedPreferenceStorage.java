package co.infinum.princeofversions;

import android.content.Context;
import android.content.SharedPreferences;

class PrinceOfVersionsDefaultNamedPreferenceStorage implements Storage {

    private static final String KEY = "PrinceOfVersions_LastNotifiedUpdate";

    private SharedPreferences sp;

    PrinceOfVersionsDefaultNamedPreferenceStorage(Context context) {
        sp = context.getSharedPreferences(context.getString(R.string.prince_of_versions_shared_preference_file), Context.MODE_PRIVATE);
    }

    @Override
    public String lastNotifiedVersion(String defaultValue) {
        return sp.getString(KEY, defaultValue);
    }

    @Override
    public void rememberLastNotifiedVersion(String version) {
        sp.edit().putString(KEY, version).apply();
    }

}
