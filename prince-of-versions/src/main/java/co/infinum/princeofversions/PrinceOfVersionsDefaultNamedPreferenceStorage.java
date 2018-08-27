package co.infinum.princeofversions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.Callable;

class PrinceOfVersionsDefaultNamedPreferenceStorage implements Storage {

    private static final String KEY = "PrinceOfVersions_LastNotifiedUpdate";

    private final SharedPreferences sp;

    PrinceOfVersionsDefaultNamedPreferenceStorage(final Context context) {
        sp = Lazy.create(SharedPreferences.class, new Callable<SharedPreferences>() {
            @Override
            public SharedPreferences call() {
                return context.getSharedPreferences(
                    context.getString(R.string.prince_of_versions_shared_preference_file),
                    Context.MODE_PRIVATE
                );
            }
        });
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
