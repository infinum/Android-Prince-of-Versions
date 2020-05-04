package co.infinum.princeofversions;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import co.infinum.princeofversions.mocks.MockApplicationConfiguration;
import co.infinum.princeofversions.mocks.MockDefaultRequirementChecker;
import co.infinum.princeofversions.util.MapUtil;
import co.infinum.princeofversions.util.ResourceUtils;

import static co.infinum.princeofversions.util.MapUtil.entry;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class JsonConfigurationParserTest {

    private static final String DEFAULT_OS_VERSION = "required_os_version";

    private JsonConfigurationParser parser;

    @Before
    public void setUp() {
        parser = new JsonConfigurationParser(new PrinceOfVersionsRequirementsProcessor());
    }

    @After
    public void tearDown() {
        parser = null;
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
                entry("key2", "value2"),
                entry("key3", "true"),
                entry("key4", "0"),
                entry("key5", "[0,1]"),
                entry("key6", "{}")
            )
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(240)
                .withRequirements(MapUtil.from(
                    entry("required_os_version", "17")
                ))
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
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
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
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
                .withOptionalNotificationType(NotificationType.ALWAYS)
                .build()
        );
    }

    @Test(expected = IllegalArgumentException.class)
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
                .withMandatoryVersion(123)
                .build()
        );
    }

    @Test
    public void validUpdateWithJsonArray() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array.json"));
        assertThat(
            config
        ).isEqualTo(
            new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
                .build()
        );
    }

    @Test
    public void validUpdateWithMergingMetadata() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_metadata.json"));
        assertThat(
            config
        ).isEqualTo(
            new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
                .withMetadata(MapUtil.from(
                    entry("x", "10"),
                    entry("z", "3")
                ))
                .build());
    }

    @Test
    public void validUpdateWithOverridingMetadata() throws Throwable {
        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_overriding_metadata.json"));
        assertThat(
            config
        ).isEqualTo(
            new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion(123)
                .withOptionalVersion(245)
                .withMetadata(MapUtil.from(
                    entry("x", "10")
                ))
                .build());
    }

    @Test(expected = Throwable.class)
    public void invalidUpdateWithStringLatestVersion() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("invalid_update_with_string_version.json"));
    }

    @Test(expected = Throwable.class)
    public void invalidUpdateWithIntNotification() throws Throwable {
        parser.parse(ResourceUtils.readFromFile("invalid_update_with_int_notification_type.json"));
    }

    @Test
    public void validUpdateWithRequirements() throws Throwable {
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(200, 13);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);
        PrinceOfVersionsRequirementsProcessor processor =
            new PrinceOfVersionsRequirementsProcessor(Collections.<String, RequirementChecker>singletonMap(DEFAULT_OS_VERSION, checker));
        JsonConfigurationParser parser =
            new JsonConfigurationParser(processor);

        PrinceOfVersionsConfig config = parser.parse(ResourceUtils.readFromFile("valid_update_full_array_with_requirements.json"));
        assertThat(
            config
        ).isEqualTo(
            new PrinceOfVersionsConfig.Builder()
                .withMandatoryVersion(123)
                .withOptionalVersion(246)
                .build());
    }

    @Test(expected = Throwable.class)
    public void invalidDataInDefaultRequirementChecker() throws JSONException {
        MockApplicationConfiguration applicationConfiguration = new MockApplicationConfiguration(200,13);
        MockDefaultRequirementChecker checker = new MockDefaultRequirementChecker(applicationConfiguration);
        checker.checkRequirements("not integer");
    }
}
