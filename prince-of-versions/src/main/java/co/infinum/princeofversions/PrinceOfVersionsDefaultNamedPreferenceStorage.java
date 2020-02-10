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

    @Nullable
    @Override
    public Integer lastNotifiedVersion(@Nullable Integer defaultValue) {
        try {
            String value = sp.getString(KEY, String.valueOf(defaultValue));
            if (value != null) {
                return Integer.valueOf(value);
            } else {
                return null;
            }
        } catch (NumberFormatException invalid) {
            return null;
        }
    }

    @Override
    public void rememberLastNotifiedVersion(@Nullable Integer version) {
        sp.edit().putString(KEY, String.valueOf(version)).apply();
    }
}
