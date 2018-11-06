package co.infinum.princeofversions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

final class PrinceOfVersionsDefaultNamedPreferenceStorage implements Storage {

    private static final String KEY = "PrinceOfVersions_LastNotifiedUpdate";
    private static final String PREF_FILE_NAME = "co.infinum.princeofversions.PREF_FILE";

    private final SharedPreferences sp;

    PrinceOfVersionsDefaultNamedPreferenceStorage(final Context context) {
        sp = Lazy.create(SharedPreferences.class, new Callable<SharedPreferences>() {
            @Override
            public SharedPreferences call() {
                return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            }
        });
    }

    @Override
    public String lastNotifiedVersion(@Nullable String defaultValue) {
        return sp.getString(KEY, defaultValue);
    }

    @Override
    public void rememberLastNotifiedVersion(@Nullable String version) {
        sp.edit().putString(KEY, version).apply();
    }
}
