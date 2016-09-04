package co.infinum.princeofversions.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import co.infinum.princeofversions.BuildConfig;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.parsers.JSONVersionConfigParser;
import co.infinum.princeofversions.interfaces.VersionRepository;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.loaders.ResourceFileLoader;
import co.infinum.princeofversions.verifiers.SingleThreadVersionVerifier;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class PrinceOfVersionsTest {

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
        versionVerifier = new SingleThreadVersionVerifier(new JSONVersionConfigParser(ContextHelper.getAppVersion(context)));
        Mockito.when(provider.newInstance()).thenReturn(versionVerifier);
        return context;
    }

    @Test
    public void testCheckingValidContentNoNotification() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_no_notification.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingValidContentNotificationAlways() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_notification_always.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingValidContentOnlyMinVersion() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_only_min_version.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingValidContentWithoutCodes() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndLessThanOptional() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsLessThanMinAndLessThanOptional() throws PackageManager.NameNotFoundException {
        Context context = setupContext("1.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("1.2.3", true);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsEqualToMinAndLessThanOptional() throws PackageManager.NameNotFoundException {
        Context context = setupContext("1.2.3");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(1)).onNewUpdate("2.4.5", false);
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndEqualToOptional() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.4.5");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingContentJSONWhenCurrentIsGreaterThanMinAndGreaterThanOptional() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.UNKNOWN_ERROR);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingInvalidContentWithInvalidVersion() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("invalid_update_invalid_version.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingInvalidContentNoAndroidKey() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("invalid_update_no_android.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingInvalidContentNoJSON() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("invalid_update_no_json.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingInvalidContentNoMinVersion() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("invalid_update_no_min_version.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingInvalidContentOptionalNoMinVersion() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("invalid_update_optional_without_version.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

    @Test
    public void testCheckingValidContentWithAlwaysNotification() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_notification_always.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingValidContentWithOnlyMinVersion() throws PackageManager.NameNotFoundException {
        Context context = setupContext("3.0.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_only_min_version.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingWhenVersionIsAlreadyNotified() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0.0");
        VersionRepository repo = Mockito.mock(VersionRepository.class);
        Mockito.when(repo.getLastVersionName(Mockito.anyString())).thenReturn("2.4.5");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repo);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(0)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(1)).onNoUpdate();
    }

    @Test
    public void testCheckingWhenCurrentAppVersionIsInvalid() throws PackageManager.NameNotFoundException {
        Context context = setupContext("2.0");
        PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository);
        updater.checkForUpdates(new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                return new ResourceFileLoader("valid_update_full.json");
            }
        }, callback);

        Mockito.verify(callback, Mockito.times(0)).onNewUpdate(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(callback, Mockito.times(1)).onError(ErrorCode.WRONG_VERSION);
        Mockito.verify(callback, Mockito.times(0)).onNoUpdate();
    }

}
