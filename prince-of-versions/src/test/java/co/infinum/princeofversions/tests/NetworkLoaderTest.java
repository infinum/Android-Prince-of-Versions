package co.infinum.princeofversions.tests;

import junit.framework.Assert;

import net.bytebuddy.implementation.bytecode.Throw;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.parsers.JsonVersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.interfaces.VersionVerifierListener;
import co.infinum.princeofversions.loaders.NetworkLoader;
import co.infinum.princeofversions.loaders.factories.NetworkLoaderFactory;
import co.infinum.princeofversions.threading.ExecutorServiceVersionVerifier;
import co.infinum.princeofversions.util.ResourceUtils;
import co.infinum.princeofversions.verifiers.SingleThreadVersionVerifier;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * Created by Juraj Pejnović on 07/09/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class NetworkLoaderTest {

    private MockWebServer mockWebServer;

    private VersionVerifier versionVerifier;

    private VersionVerifierFactory provider;

    private UpdaterCallback callback;

    private VersionRepository repository;


    private Context setupContext(String versionName) throws PackageManager.NameNotFoundException {
        Context context = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(context.getPackageName()).thenReturn("name");
        PackageInfo packageInfo = Mockito.mock(PackageInfo.class);
        packageInfo.versionName = versionName;
        Mockito.when(context.getPackageManager().getPackageInfo("name", 0)).thenReturn(packageInfo);
        versionVerifier = new ExecutorServiceVersionVerifier(new JsonVersionConfigParser(ContextHelper.getAppVersion(context)));
        Mockito.when(provider.newInstance()).thenReturn(versionVerifier);
        return context;
    }

    @Before
    public void setup() throws IOException {
        callback = Mockito.mock(UpdaterCallback.class);
        provider = Mockito.mock(VersionVerifierFactory.class);

        ShadowLog.stream = System.out;
        repository = Mockito.mock(VersionRepository.class);
        Mockito.when(repository.getLastVersionName(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(repository).setLastVersionName(Mockito.anyString());

        mockWebServer = new MockWebServer();
        mockWebServer.start();

    }

    @Test
    public void networkNormalTest() throws IOException, InterruptedException {
        MockResponse response = new MockResponse().setBody(ResourceUtils.readFromFile("valid_update_full.json")).setResponseCode(200);

        mockWebServer.enqueue(response);

        UpdateConfigLoader networkLoader = new NetworkLoaderFactory(mockWebServer.url("/").toString()).newInstance();

        String content = networkLoader.load();

        Assert.assertEquals(content.replace("\n", ""), ResourceUtils.readFromFile("valid_update_full.json").replace("\n", ""));

    }

    @Test
    public void networkTimeoutTest() throws PackageManager.NameNotFoundException, InterruptedException {

        SingleThreadVersionVerifier versionVerifier = new SingleThreadVersionVerifier(
                new JsonVersionConfigParser(ContextHelper.getAppVersion(setupContext("2.0.0"))));

        UpdateConfigLoader loader = new NetworkLoader(mockWebServer.url("/").toString()) {
            @Override
            public String load() throws IOException, InterruptedException {
                throw new IOException();
            }
        };

        VersionVerifierListener listener = new VersionVerifierListener() {
            @Override
            public void versionAvailable(VersionContext version) {
                callback.onNewUpdate(version.getCurrentVersion().toString(), true, version.getMetadata());
            }

            @Override
            public void versionUnavailable(@ErrorCode int error, Throwable throwable) {
                callback.onError(error, throwable);
            }
        };

        versionVerifier.verify(loader, listener);

        verify(callback, timeout(20000).times(1)).onError(ArgumentMatchers.eq(ErrorCode.LOAD_ERROR), ArgumentMatchers.any(Throwable.class));
        verify(callback, never()).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean(), ArgumentMatchers.<String, String>anyMap());
        verify(callback, never()).onNoUpdate(ArgumentMatchers.<String, String>anyMap());
    }


    @Test
    public void networkJsonMalformedTest() throws IOException, InterruptedException, PackageManager.NameNotFoundException {
        MockResponse response = new MockResponse().setBody(ResourceUtils.readFromFile("malformed_json.json")).setResponseCode(200);

        mockWebServer.enqueue(response);

        UpdateConfigLoader networkLoader = new NetworkLoaderFactory(mockWebServer.url("/").toString()).newInstance();

        String content = networkLoader.load();

        Assert.assertEquals(content.replace("\n", ""), ResourceUtils.readFromFile("malformed_json.json").replace("\n", ""));

    }

    @Test
    public void networkResponseMissingTest() throws IOException, InterruptedException, PackageManager.NameNotFoundException {
        MockResponse response = new MockResponse().setResponseCode(200);

        mockWebServer.enqueue(response);

        UpdateConfigLoader networkLoader = new NetworkLoaderFactory(mockWebServer.url("/").toString()).newInstance();

        String content = networkLoader.load();

        Assert.assertEquals(content.replace("\n", ""), "");

    }

    @After
    public void cleanup() throws IOException {
        try {
            mockWebServer.shutdown();
        } catch (Exception ignorable) {

        }

    }
}
