package co.infinum.princeofversions.tests;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.parsers.JsonVersionConfigParser;
import co.infinum.princeofversions.util.ResourceUtils;

/**
 * Created by Juraj Pejnović on 08/09/16.
 */
@RunWith(RobolectricTestRunner.class)
public class MetadataTest {

    @Test
    public void metadataNormalTest() throws JSONException {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        JsonVersionConfigParser parser = new JsonVersionConfigParser(currentVersion);
        JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full_with_metadata.json"));
        VersionContext version = parser.parse(parsedResponseContent);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Assert.assertEquals(data, version.getMetadata());

    }

    @Test
    public void metadataNullTest() throws JSONException {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        JsonVersionConfigParser parser = new JsonVersionConfigParser(currentVersion);
        JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full_with_metadata_null.json"));
        VersionContext version = parser.parse(parsedResponseContent);

        Map<String, String> data = new HashMap<>();

        Assert.assertEquals(version.getMetadata(), data);
    }

    @Test
    public void noMetadataTest() throws JSONException {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        JsonVersionConfigParser parser = new JsonVersionConfigParser(currentVersion);
        JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full.json"));
        VersionContext version = parser.parse(parsedResponseContent);

        Map<String, String> data = new HashMap<>();

        Assert.assertEquals(version.getMetadata(), data);
    }

    @Test
    public void metadataValuesMalformed() throws JSONException {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        JsonVersionConfigParser parser = new JsonVersionConfigParser(currentVersion);
        JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full_with_metadata_malformed.json"));
        VersionContext version = parser.parse(parsedResponseContent);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Assert.assertEquals(version.getMetadata(), data);
    }
}
