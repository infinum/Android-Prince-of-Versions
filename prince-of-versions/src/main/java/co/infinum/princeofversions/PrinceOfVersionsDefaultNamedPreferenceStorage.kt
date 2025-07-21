package co.infinum.princeofversions

import android.content.Context
import android.content.SharedPreferences

internal class PrinceOfVersionsDefaultNamedPreferenceStorage(context: Context) : Storage {

    private companion object {
        private const val KEY = "PrinceOfVersions_LastNotifiedUpdate"
        private const val PRINCE_OF_VERSIONS_PREFERENCES = "co.infinum.princeofversions.PREF_FILE"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PRINCE_OF_VERSIONS_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun lastNotifiedVersion(defaultValue: Int?): Int? {
        val value = sharedPreferences.getString(KEY, defaultValue.toString())
        return value?.toIntOrNull()
    }

    override fun rememberLastNotifiedVersion(version: Int?) {
        version?.let { sharedPreferences.edit().putString(KEY, version.toString()).apply() }
    }
}
