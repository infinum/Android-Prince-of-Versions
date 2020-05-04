package co.infinum.queenofversions;

import android.content.Context;
import android.content.SharedPreferences;
import co.infinum.princeofversions.Storage;
import javax.annotation.Nullable;

final class QueenOfVersionsDefaultNamedPreferenceStorage implements Storage {

    private static final String KEY = "QueenOfVersions_LastNotifiedUpdate";

    private static final String PREF_FILE_NAME = "co.infinum.queenofversions.PREF_FILE";

    private final Context context;

    @Nullable
    private SharedPreferences sp = null;

    QueenOfVersionsDefaultNamedPreferenceStorage(final Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public Integer lastNotifiedVersion(@Nullable Integer defaultValue) {
        try {
            String value = getPreferences().getString(KEY, String.valueOf(defaultValue));
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
        getPreferences().edit().putString(KEY, String.valueOf(version)).apply();
    }

    private SharedPreferences getPreferences() {
        SharedPreferences local = this.sp;
        if (local == null) {
            synchronized (this) {
                local = this.sp;
                if (local == null) {
                    this.sp = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return sp;
    }
}
