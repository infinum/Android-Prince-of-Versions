package co.infinum.princeofversions;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import co.infinum.princeofversions.util.MapUtil;
import co.infinum.princeofversions.util.ResourceUtils;

import static co.infinum.princeofversions.util.MapUtil.entry;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class JsonConfigurationParserTest {



    private JsonConfigurationParser parser;

    @Before
    public void setUp() {
        parser = new JsonConfigurationParser(new PrinceOfVersionsDefaultRequirementsChecker());
    }

    /*

    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testIsNonEmptyWithValue() {
        assertThat(parser.isNonEmpty("value")).isTrue();
    }

    @Test
    public void testIsNonEmptyWithEmpty() {
        assertThat(parser.isNonEmpty("")).isFalse();
    }


    @Test
    public void testIsNonEmptyWithSpace() {
        assertThat(parser.isNonEmpty(" ")).isFalse();
    }

    @Test
    public void testIsNonEmptyWithNull() {
        assertThat(parser.isNonEmpty("null")).isFalse();
    }

    @Test
    public void checkEmptyJsonToStringMap() throws Throwable {
        assertThat(parser.jsonObjectToMap(new JSONObject("{}"))).isEmpty();
    }

    @Test
    public void checkJsonToStringMap() throws Throwable {
        assertThat(
                parser.jsonObjectToMap(new JSONObject(ResourceUtils.readFromFile("json_obj_string.json")))
        ).isEqualTo(
                MapUtil.from(
                        entry("key1", "value1"),
                        entry("key2", "value2")
                )
        );
    }

    @Test
    public void checkComplexJsonToStringMap() throws Throwable {
        assertThat(
                parser.jsonObjectToMap(new JSONObject(ResourceUtils.readFromFile("json_obj_string_complex.json")))
        ).isEqualTo(
                MapUtil.from(
                        entry("key1", "value1"),
                        entry("key2", "value2")
                )
        );
    }

    @Test
    public void invalidUpdateInvalidVersion() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("invalid_update_invalid_version.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void invalidUpdateNoAndroid() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("invalid_update_no_android.json"));
    }

    @Test(expected = JSONException.class)
    public void invalidUpdateNotJson() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("invalid_update_no_json.json"));
    }

    @Test(expected = JSONException.class)
    public void malformedJson() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("malformed_json.json"));
    }

    @Test
    public void validUpdateFullJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateFullBJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_b.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3-b12")
                        .withOptionalVersion("2.4.5-b45")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateFullBWithSdkValues() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_b_with_sdk_values.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3-b12")
                        .withMandatoryMinSdk(15)
                        .withOptionalVersion("2.4.5-b45")
                        .withOptionalMinSdk(16)
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateFullBetaJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_beta.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3-beta2")
                        .withOptionalVersion("2.4.5-beta3")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateFullRcJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_rc.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3-rc2")
                        .withOptionalVersion("2.4.5-rc3")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateFullWithMetadataJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .withMetadata(MapUtil.from(
                                entry("key1", "value1"),
                                entry("key2", "value2")
                        ))
                        .build()
        );
    }

    @Test
    public void validUpdateFullWithEmptyMetadataJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_empty.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .withMetadata(new HashMap<String, String>())
                        .build()
        );
    }

    @Test(expected = JSONException.class)
    public void validUpdateFullWithMetadataMalformed() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_malformed.json"));
    }

    @Test
    public void validUpdateFullWithNullMetadataJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_metadata_null.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .withMetadata(new HashMap<String, String>())
                        .build()
        );
    }

    @Test
    public void validUpdateFullWithSdkValuesJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_with_sdk_values.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withMandatoryMinSdk(15)
                        .withOptionalVersion("2.4.0")
                        .withOptionalMinSdk(17)
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void noMandatoryVersionJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_no_min_version.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateNoNotificationJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_no_notification.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ONCE)
                        .build()
        );
    }

    @Test
    public void validUpdateAlwaysNotificationJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_notification_always.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .withOptionalVersion("2.4.5")
                        .withOptionalNotificationType(NotificationType.ALWAYS)
                        .build()
        );
    }

    @Test(expected = JSONException.class)
    public void mandatoryVersionNullJson() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("valid_update_null_min_version.json"));
    }

    @Test
    public void validUpdateOnlyMandatoryJson() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_only_min_version.json"));
        assertThat(
                config
        ).isEqualTo(
                new PrinceOfVersionsConfig.Builder()
                        .withMandatoryVersion("1.2.3")
                        .build()
        );
    }*/

}
