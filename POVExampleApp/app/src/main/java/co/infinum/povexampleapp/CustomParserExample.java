package co.infinum.povexampleapp;

import com.github.zafarkhaja.semver.Version;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.infinum.princeofversions.DefaultUpdater;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.LoaderValidationException;
import co.infinum.princeofversions.PrinceOfVersionsContext;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.common.VersionContext;
import co.infinum.princeofversions.exceptions.ParseException;
import co.infinum.princeofversions.helpers.ContextHelper;
import co.infinum.princeofversions.helpers.parsers.VersionConfigParser;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.interfaces.VersionVerifier;
import co.infinum.princeofversions.interfaces.VersionVerifierFactory;
import co.infinum.princeofversions.network.NetworkLoaderFactory;
import co.infinum.princeofversions.threading.ThreadVersionVerifier;

public class CustomParserExample extends BaseExampleActivity {

    public static final String TAG = "POV_CUSTOM_PARSER";

    private UpdateChecker updater;
    private LoaderFactory loaderFactory;
    private PrinceOfVersionsContext povContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_usage);
        ButterKnife.bind(this);

        /*  create new instance of updater associated with application context   */
        updater = new DefaultUpdater(this, versionVerifierFactory);
        /*  create specific loader factory for loading from internet    */
        loaderFactory = new NetworkLoaderFactory("http://pastebin.com/raw/7shyuaWu");
    }

    @OnClick(R.id.btnCheck)
    public void onCheckClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        PrinceOfVersionsContext context = updater.checkForUpdates(loaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    @OnClick(R.id.btnCancelTest)
    public void onCancelTestClick() {
        /*  same call as few lines higher, but using another loader, this one is very slow loader  */
        PrinceOfVersionsContext context = updater.checkForUpdates(slowLoaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    @OnClick(R.id.btnCancel)
    public void onCancelClick() {
        PrinceOfVersionsContext context;
        /*  cancel current checking request, checking if context is not consumed yet is not necessary   */
        if (povContext != null && !povContext.isConsumed()) {
            povContext.cancel();
        }
    }

    private void replacePOVContext(PrinceOfVersionsContext povContext) {
        /*  started new checking, kill current one if not dead and remember new context */
        if (this.povContext != null && !this.povContext.isConsumed() && !this.povContext.isCancelled()) {
            toastIt(getString(R.string.replace), Toast.LENGTH_SHORT);
            this.povContext.cancel();
        }
        this.povContext = povContext;
    }

    /**
     * This factory creates a very slow loader, just to give us enough time to invoke cancel option.
     */
    private LoaderFactory slowLoaderFactory = new LoaderFactory() {
        @Override
        public UpdateConfigLoader newInstance() {

            final UpdateConfigLoader instance = loaderFactory.newInstance();

            return new UpdateConfigLoader() {
                @Override
                public String load() throws IOException, InterruptedException {
                    Thread.sleep(2000);
                    return instance.load();
                }

                @Override
                public void cancel() {
                    instance.cancel();
                }

                @Override
                public void validate() throws LoaderValidationException {
                    instance.validate();
                }
            };
        }
    };

    private VersionVerifierFactory versionVerifierFactory = new VersionVerifierFactory() {
        @Override
        public VersionVerifier newInstance() {
            return new ThreadVersionVerifier(
                    new VersionConfigParser() {

                        @Override
                        public VersionContext parse(String content) throws ParseException {
                            try {
                                VersionContext.Version appVersion = ContextHelper.getAppVersion(CustomParserExample.this);
                                Version current = Version.valueOf(appVersion.getVersionString());
                                VersionContext.Version minVersion = new VersionContext.Version(
                                        new JSONObject(content).getString("minimum__version"));
                                Version minimum = Version.valueOf(minVersion.getVersionString());
                                VersionContext vc = new VersionContext(
                                        appVersion,
                                        minVersion,
                                        current.lessThan(minimum)
                                );
                                return vc;
                            } catch (PackageManager.NameNotFoundException | JSONException e) {
                                Log.d(TAG, content);
                                throw new ParseException(e);
                            }
                        }
                    }
            );
        }
    };

}
