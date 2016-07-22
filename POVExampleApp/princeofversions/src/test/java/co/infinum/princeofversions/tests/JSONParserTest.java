package co.infinum.princeofversions.tests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.util.ResourceUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class JSONParserTest {

    private JSONObject parsedResponseContent;

    @Before
    public void setUp() throws JSONException {
        parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update.json"));

    }

    @Test
    public void testParsingResponseJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() {
        String currentVersion = "2.0.0";
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 2.0.0", "2.0.0", version.getCurrentVersion());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", "14235", version.getMinimumVersion().getVersionCode());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", "42354", version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingResponseJSONWhenCurrentIsLessThanMinAndLessThanOptional() {
        String currentVersion = "1.0.0";
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 1.0.0", "1.0.0", version.getCurrentVersion());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", "14235", version.getMinimumVersion().getVersionCode());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", "42354", version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertTrue("Current version should be less than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingResponseJSONWhenCurrentIsEqualToMinAndLessThanOptional() {
        String currentVersion = "1.2.3";
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 1.2.3", "1.2.3", version.getCurrentVersion());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", "14235", version.getMinimumVersion().getVersionCode());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", "42354", version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should not be less than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingResponseJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() {
        String currentVersion = "2.4.5";
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 2.4.5", "2.4.5", version.getCurrentVersion());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", "14235", version.getMinimumVersion().getVersionCode());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", "42354", version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should not be less than minimum", version.isCurrentLessThanMinimum());
            assertFalse("Current version should not be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingResponseJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() {
        String currentVersion = "3.0.0";
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", "14235", version.getMinimumVersion().getVersionCode());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", "42354", version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
            assertFalse("Current version should be greater than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }


}
