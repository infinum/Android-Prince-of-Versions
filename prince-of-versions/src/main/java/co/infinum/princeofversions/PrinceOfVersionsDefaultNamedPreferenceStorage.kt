package co.infinum.princeofversions

import android.content.Context
import android.content.SharedPreferences

internal class PrinceOfVersionsDefaultNamedPreferenceStorage(context: Context) : Storage {

    private companion object {
        private const val KEY = "PrinceOfVersions_LastNotifiedUpdate"
        private const val PREF_FILE_NAME = "co.infinum.princeofversions.PREF_FILE"
    }

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun lastNotifiedVersion(defaultValue: Int?): Int? {
        val value = sp.getString(KEY, defaultValue.toString())
        return value?.toIntOrNull()
    }

    override fun rememberLastNotifiedVersion(version: Int?) = sp.edit().putString(KEY, version.toString()).apply()
}
