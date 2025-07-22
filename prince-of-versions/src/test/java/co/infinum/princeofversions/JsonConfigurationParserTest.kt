package co.infinum.princeofversions

import co.infinum.princeofversions.mocks.MockDefaultRequirementChecker
import co.infinum.princeofversions.util.ResourceUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.json.JSONException
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class JsonConfigurationParserTest {

    private lateinit var parser: JsonConfigurationParser

    @Before
    fun setUp() {
        val defaultRequirements: Map<String, RequirementChecker> = mapOf(
            PrinceOfVersionsDefaultRequirementsChecker.KEY to MockDefaultRequirementChecker(21)
        )
        parser = JsonConfigurationParser(PrinceOfVersionsRequirementsProcessor(defaultRequirements))
    }

    @Test
    fun checkEmptyJsonToStringMap() {
        assertThat(parser.jsonObjectToMap(JSONObject("{}"))).isEmpty()
    }

    @Test
    fun checkJsonToStringMap() {
        val map = parser.jsonObjectToMap(JSONObject(ResourceUtils.readFromFile("json_obj_string.json")))
        assertThat(map).isEqualTo(mapOf("key1" to "value1", "key2" to "value2"))
    }

    @Test
    fun checkJsonToStringMapWithNull() {
        val map = parser.jsonObjectToMap(JSONObject(ResourceUtils.readFromFile("json_obj_string_with_null.json")))
        assertThat(map).isEqualTo(mapOf("key1" to null, "key2" to "value2"))
    }

    @Test
    fun checkComplexJsonToStringMap() {
        val map = parser.jsonObjectToMap(JSONObject(ResourceUtils.readFromFile("json_obj_string_complex.json")))
        assertThat(map).isEqualTo(
            mapOf(
                "key1" to "value1",
                "key2" to "value2",
                "key3" to "true",
                "key4" to "0",
                "key5" to "[0,1]",
                "key6" to "{}"
            )
        )
    }

    @Test
    fun invalidUpdateNoAndroidKey() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("invalid_update_no_android.json")) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessage("Config resource does not contain android key")
    }

    @Test
    fun invalidUpdateNotJson() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("invalid_update_no_json.json")) }
            .isInstanceOf(JSONException::class.java)
    }

    @Test
    fun malformedJson() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("malformed_json.json")) }
            .isInstanceOf(JSONException::class.java)
    }

    @Test
    fun validUpdateFullJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateFullWithMetadataJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .withMetadata(mapOf("key1" to "value1", "key2" to "value2"))
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateFullWithEmptyMetadataJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_empty.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .withMetadata(emptyMap())
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateFullWithMetadataMalformed() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_malformed.json")) }
            .isInstanceOf(JSONException::class.java)
    }

    @Test
    fun validUpdateFullWithNullMetadataJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_null.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .withMetadata(emptyMap())
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateFullWithSdkValuesJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_sdk_values.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(240)
            .withRequirements(mapOf("required_os_version" to "17"))
            .withOptionalNotificationType(NotificationType.ONCE)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun noMandatoryVersionJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_no_min_version.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateNoNotificationJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_no_notification.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ONCE)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateAlwaysNotificationJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_notification_always.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withOptionalNotificationType(NotificationType.ALWAYS)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun mandatoryVersionNullJson() {
        val result = parser.parse(ResourceUtils.readFromFile("valid_update_null_min_version.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(null)
            .withOptionalVersion(245)
            .build()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun validUpdateOnlyMandatoryJson() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_only_min_version.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateWithJsonArray() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateWithMergingMetadata() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_metadata.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withMetadata(mapOf("x" to "10", "z" to "3"))
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun validUpdateWithOverridingMetadata() {
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_overriding_metadata.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(245)
            .withMetadata(mapOf("x" to "10"))
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun invalidUpdateWithStringLatestVersion() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("invalid_update_with_string_version.json")) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun invalidUpdateWithIntNotification() {
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("invalid_update_with_int_notification_type.json")) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun validUpdateWithRequirements() {
        val checker = MockDefaultRequirementChecker(13)
        val requirements: Map<String, RequirementChecker> = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker)
        val processor = PrinceOfVersionsRequirementsProcessor(requirements)
        val parser = JsonConfigurationParser(processor)
        val config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_requirements.json"))
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123)
            .withOptionalVersion(246)
            .withRequirements(mapOf("required_os_version" to "13"))
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun invalidDataInDefaultRequirementChecker() {
        val checker = MockDefaultRequirementChecker(13)
        assertThatThrownBy { checker.checkRequirements("not integer") }
            .isInstanceOf(Throwable::class.java)
    }

    @Test
    fun checkAndroid2KeyIsUsedOverAndroidKey() {
        val json = """
            {
                "android2": { "required_version": 999 },
                "android": { "required_version": 123 }
            }
        """.trimIndent()
        val config = parser.parse(json)
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(999) // Version from 'android2'
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun checkAndroidKeyIsUsedAsFallback() {
        val json = """
            {
                "android": { "required_version": 123 }
            }
        """.trimIndent()
        val config = parser.parse(json)
        val expected = PrinceOfVersionsConfig.Builder()
            .withMandatoryVersion(123) // Version from 'android'
            .build()
        assertThat(config).isEqualTo(expected)
    }

    @Test
    fun checkRequirementsNotSatisfiedInArrayThrowsException() {
        val checker = MockDefaultRequirementChecker(10) // Device SDK is 10
        val requirements: Map<String, RequirementChecker> = mapOf(PrinceOfVersionsDefaultRequirementsChecker.KEY to checker)
        val processor = PrinceOfVersionsRequirementsProcessor(requirements)
        val parser = JsonConfigurationParser(processor)
        // JSON requires SDK 13 and 21, neither is satisfied
        assertThatThrownBy { parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_requirements.json")) }
            .isInstanceOf(RequirementsNotSatisfiedException::class.java)
    }
}
