package co.infinum.princeofversions.tests;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Build;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.util.ResourceUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class JSONParserTest {

    @Test
    public void testParsingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() {
        VersionContext.Version currentVersion = new VersionContext.Version("2.0.0");
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 2.0.0", "2.0.0", version.getCurrentVersion().getVersionString());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
            assertTrue("Optional version should be available", version.hasOptionalUpdate());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingContentJSONWhenCurrentIsLessThanMinAndLessThanOptional() {
        VersionContext.Version currentVersion = new VersionContext.Version("1.0.0");
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 1.0.0", "1.0.0", version.getCurrentVersion().getVersionString());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
            assertTrue("Optional version should be available", version.hasOptionalUpdate());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertTrue("Current version should be less than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingContentJSONWhenCurrentIsEqualToMinAndLessThanOptional() {
        VersionContext.Version currentVersion = new VersionContext.Version("1.2.3");
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 1.2.3", "1.2.3", version.getCurrentVersion().getVersionString());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
            assertTrue("Optional version should be available", version.hasOptionalUpdate());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should not be less than minimum", version.isCurrentLessThanMinimum());
            assertTrue("Current version should be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 2.4.5", "2.4.5", version.getCurrentVersion().getVersionString());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
            assertTrue("Optional version should be available", version.hasOptionalUpdate());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should not be less than minimum", version.isCurrentLessThanMinimum());
            assertFalse("Current version should not be less than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        try {
            JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
            JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
            VersionContext version = parser.parse(parsedResponseContent);
            assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion().getVersionString());
            assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
            assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
            assertTrue("Optional version should be available", version.hasOptionalUpdate());
            assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
            assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
            assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
            assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
            assertFalse("Current version should be greater than optional", version.isCurrentLessThanOptional());
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = ParseException.class)
    public void testParsingInvalidContentWithInvalidVersion() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        parser.parse(ResourceUtils.readFromFile("invalid_update_invalid_version.json"));
    }

    @Test(expected = ParseException.class)
    public void testParsingInvalidContentNoAndroidKey() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        parser.parse(ResourceUtils.readFromFile("invalid_update_no_android.json"));
    }

    @Test(expected = ParseException.class)
    public void testParsingInvalidContentNoJSON() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        parser.parse(ResourceUtils.readFromFile("invalid_update_no_json.json"));
    }

    @Test(expected = ParseException.class)
    public void testParsingInvalidContentNoMinVersion() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        parser.parse(ResourceUtils.readFromFile("invalid_update_no_min_version.json"));
    }

    @Test(expected = ParseException.class)
    public void testParsingInvalidContentOptionalNoMinVersion() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        parser.parse(ResourceUtils.readFromFile("invalid_update_optional_without_version.json"));
    }

    @Test
    public void testParsingValidContentNoNotification() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        VersionContext version = parser.parse(ResourceUtils.readFromFile("valid_update_no_notification.json"));
        assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion().getVersionString());
        assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
        assertEquals("Minimum version code should be 14235", 14235, version.getMinimumVersion().getVersionCode());
        assertTrue("Optional version should be available", version.hasOptionalUpdate());
        assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
        assertEquals("Optional version code should be 42354", 42354, version.getOptionalUpdate().getVersion().getVersionCode());
        assertEquals("Optional notification type should be ALWAYS", "ALWAYS", version.getOptionalUpdate().getNotificationType());
        assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
        assertFalse("Current version should be greater than optional", version.isCurrentLessThanOptional());
    }

    @Test
    public void testParsingValidContentWithAlwaysNotification() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        VersionContext version = parser.parse(ResourceUtils.readFromFile("valid_update_notification_always.json"));
        assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion().getVersionString());
        assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
        assertTrue("Optional version should be available", version.hasOptionalUpdate());
        assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
        assertEquals("Optional notification type should be ALWAYS", "ALWAYS", version.getOptionalUpdate().getNotificationType());
        assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
        assertFalse("Current version should be greater than optional", version.isCurrentLessThanOptional());
    }

    @Test
    public void testParsingValidContentWithOnlyMinVersion() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        VersionContext version = parser.parse(ResourceUtils.readFromFile("valid_update_only_min_version.json"));
        assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion().getVersionString());
        assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
        assertFalse("Optional version should not be available", version.hasOptionalUpdate());
    }

    @Test
    public void testParsingValidContentWithoutCodes() throws ParseException {
        VersionContext.Version currentVersion = new VersionContext.Version("3.0.0");
        JSONVersionConfigParser parser = new JSONVersionConfigParser(currentVersion);
        VersionContext version = parser.parse(ResourceUtils.readFromFile("valid_update_without_codes.json"));
        assertEquals("Current version should be 3.0.0", "3.0.0", version.getCurrentVersion().getVersionString());
        assertEquals("Minimum version should be 1.2.3", "1.2.3", version.getMinimumVersion().getVersionString());
        assertTrue("Optional version should be available", version.hasOptionalUpdate());
        assertEquals("Optional version should be 2.4.5", "2.4.5", version.getOptionalUpdate().getVersion().getVersionString());
        assertEquals("Optional notification type should be ONCE", "ONCE", version.getOptionalUpdate().getNotificationType());
        assertFalse("Current version should be greater than minimum", version.isCurrentLessThanMinimum());
        assertFalse("Current version should be greater than optional", version.isCurrentLessThanOptional());
    }

}
