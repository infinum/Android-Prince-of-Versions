package co.infinum.princeofversions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

/**
 * Implementation of {@link Storage} which stores data in default {@link SharedPreferences}.
 *
 * @deprecated due to possibility of value erasure in shared storage. Use {@link
 * PrinceOfVersionsDefaultNamedPreferenceStorage} and migrate existing data to it.
 */
@Deprecated
final class PrinceOfVersionsDefaultStorage implements Storage {

    private static final String KEY = "PrinceOfVersions_LastNotifiedUpdate";

    private final SharedPreferences sp;

    PrinceOfVersionsDefaultStorage(final Context context) {
        sp = Lazy.create(SharedPreferences.class, new Callable<SharedPreferences>() {
            @Override
            public SharedPreferences call() {
                return PreferenceManager.getDefaultSharedPreferences(context);
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
