package co.infinum.povexampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.network.NetworkLoaderFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "exampleappActivityMain";

    private UpdateChecker updater;
    private LoaderFactory loaderFactory;
    private PrinceOfVersionsContext povContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*  create new instance of updater associated with application context   */
        updater = new DefaultUpdater(MainActivity.this);
        /*  create specific loader factory for loading from internet    */
        loaderFactory = new NetworkLoaderFactory("http://pastebin.com/raw/41N8stUD");
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
        if (this.povContext != null && !this.povContext.isConsumed()) {
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

    /**
     * Callback definition for accepting updater's result. Just toast it result.
     */
    private UpdaterCallback defaultCallback = new UpdaterCallback() {
        @Override
        public void onNewUpdate(String version, boolean isMandatory) {
            Log.d("thread", "v: " + version + ", m: " + isMandatory);
            Log.d(TAG, "v: " + version + ", m: " + isMandatory);
            toastIt(
                    getString(
                            R.string.update_available_msg,
                            getString(isMandatory ? R.string.mandatory : R.string.not_mandatory),
                            version
                    ),
                    Toast.LENGTH_SHORT
            );
        }

        @Override
        public void onNoUpdate() {
            Log.d(TAG, "No update available.");
            toastIt(getString(R.string.no_update_available), Toast.LENGTH_SHORT);
        }

        @Override
        public void onError(@ErrorCode int error) {
            Log.d(TAG, "Error: " + error);
            toastIt(String.format(getString(R.string.update_error), error), Toast.LENGTH_SHORT);
        }
    };

    private void toastIt(final String message, final int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, duration).show();
            }
        });
    }
}
