package co.infinum.princeofversions.tests;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.parsers.JsonVersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.loaders.ResourceFileLoader;
import co.infinum.princeofversions.util.ResourceUtils;
import co.infinum.princeofversions.verifiers.SingleThreadVersionVerifier;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;

/**
 * Created by Juraj Pejnović on 08/09/16.
 */
@RunWith(RobolectricTestRunner.class)
public class MetadataTest {

    private UpdaterCallback callback;

    private VersionVerifier versionVerifier;

    private VersionVerifierFactory provider;

    private VersionRepository repository;

    @Before
    public void setUp() {
        callback = Mockito.mock(UpdaterCallback.class);
        provider = Mockito.mock(VersionVerifierFactory.class);
        repository = Mockito.mock(VersionRepository.class);
        Mockito.when(repository.getLastVersionName(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(repository).setLastVersionName(Mockito.anyString());

    }

    private Context setupContext(String versionName) throws PackageManager.NameNotFoundException {
        Context context = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(context.getPackageName()).thenReturn("name");
        PackageInfo packageInfo = Mockito.mock(PackageInfo.class);
        packageInfo.versionName = versionName;
        Mockito.when(context.getPackageManager().getPackageInfo("name", 0)).thenReturn(packageInfo);
        versionVerifier = new SingleThreadVersionVerifier(new JsonVersionConfigParser(ContextHelper.getAppVersion(context)));
        Mockito.when(provider.newInstance()).thenReturn(versionVerifier);
        return context;
    }

    @Test
    public void metadataPassedToOnNewUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void metadataPassedToOnNoUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("40.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(data);
    }

    @Test
    public void malformedMetadataPassedToOnNewUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_malformed.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void malformedMetadataPassedToOnNoUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("40.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_malformed.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(data);
    }

    @Test
    public void emptyMetadataPassedToOnNewUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_empty.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void emptyMetadataPassedToOnNoUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("40.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_empty.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(data);
    }

    @Test
    public void noMetadataPassedToOnNewUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void noMetadataPassedToOnNoUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("40.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(data);
    }

    @Test
    public void nullMetadataPassedToOnNewUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_null.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate(anyMap());
    }

    @Test
    public void nullMetadataPassedToOnNoUpdateCallbackTest() throws PackageManager.NameNotFoundException {
        Context context = setupContext("40.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full_with_metadata_null.json");
            }
        }, callback);

        Map<String, String> data = new HashMap<>();

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate("2.4.5", false, data);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate(data);
    }

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
    public void metadataEmptyTest() throws JSONException {
        VersionContext.Version currentVersion = new VersionContext.Version("2.4.5");
        JsonVersionConfigParser parser = new JsonVersionConfigParser(currentVersion);
        JSONObject parsedResponseContent = new JSONObject(ResourceUtils.readFromFile("valid_update_full_with_metadata_empty.json"));
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
