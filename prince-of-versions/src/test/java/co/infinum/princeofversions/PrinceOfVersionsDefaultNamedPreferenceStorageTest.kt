package co.infinum.princeofversions

import android.content.Context
import android.content.SharedPreferences
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PrinceOfVersionsDefaultNamedPreferenceStorageTest {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    }

    @Test
    fun testRememberLastNotifiedVersionStoresValue() {
        val storage = PrinceOfVersionsDefaultNamedPreferenceStorage(context)
        storage.rememberLastNotifiedVersion(123)
        verify(editor).putString("PrinceOfVersions_LastNotifiedUpdate", "123")
        verify(editor).apply()
    }

    @Test
    fun testLastNotifiedVersionReturnsStoredValue() {
        `when`(sharedPreferences.getString("PrinceOfVersions_LastNotifiedUpdate", "456")).thenReturn("123")
        val storage = PrinceOfVersionsDefaultNamedPreferenceStorage(context)
        val result = storage.lastNotifiedVersion(456)
        assertThat(result).isEqualTo(123)
    }

    @Test
    fun testLastNotifiedVersionReturnsDefaultWhenNull() {
        `when`(sharedPreferences.getString("PrinceOfVersions_LastNotifiedUpdate", "789")).thenReturn(null)
        val storage = PrinceOfVersionsDefaultNamedPreferenceStorage(context)
        val result = storage.lastNotifiedVersion(789)
        assertThat(result).isNull()
    }

    @Test
    fun testLastNotifiedVersionReturnsNullOnNumberFormatException() {
        `when`(sharedPreferences.getString("PrinceOfVersions_LastNotifiedUpdate", "999")).thenReturn("not_a_number")
        val storage = PrinceOfVersionsDefaultNamedPreferenceStorage(context)
        val result = storage.lastNotifiedVersion(999)
        assertThat(result).isNull()
    }
}
