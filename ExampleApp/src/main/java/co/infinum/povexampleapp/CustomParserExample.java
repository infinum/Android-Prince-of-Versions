package co.infinum.povexampleapp;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

import co.infinum.princeofversions.ConfigurationParser;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.NetworkLoader;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.PrinceOfVersionsCall;
import co.infinum.princeofversions.PrinceOfVersionsConfig;
import co.infinum.princeofversions.Result;
import co.infinum.princeofversions.UpdaterCallback;

public class CustomParserExample extends AppCompatActivity {

    protected UpdaterCallback defaultCallback = new UpdaterCallback() {
        @Override
        public void onNewUpdate(String version, boolean isMandatory, Map<String, String> metadata) {
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
        public void onNoUpdate(Map<String, String> metadata) {
            toastIt(getString(R.string.no_update_available), Toast.LENGTH_SHORT);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            toastIt(String.format(getString(R.string.update_exception), throwable.getMessage()), Toast.LENGTH_SHORT);
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper());

    private PrinceOfVersions updater;

    private Loader loader;

    private Loader slowLoader;

    private PrinceOfVersionsCall call;

    /**
     * Custom parser factory, used for parsing in special format.
     * Custom parser is defined for JSON object containing only one key: minimum_version.
     */
    private ConfigurationParser customParser = new ConfigurationParser() {

        private static final String MINIMUM_VERSION = "minimum_version";

        @Override
        public PrinceOfVersionsConfig parse(String value) throws Throwable {
            return new PrinceOfVersionsConfig.Builder().withMandatoryVersion(new JSONObject(value).getString(MINIMUM_VERSION)).build();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_usage);
        initUI();

        /*  create new instance of updater using custom parser factory   */
        updater = new PrinceOfVersions.Builder().withParser(customParser).build(this);
        /*  create specific loader factory for loading from internet    */
        loader = new NetworkLoader("http://pastebin.com/raw/c4c4pPyn");
        slowLoader = createSlowLoader(loader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onCancelClick();
    }

    private void initUI() {
        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        Button btnCancelTest = (Button) findViewById(R.id.btnCancelTest);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnCheckSync = (Button) findViewById(R.id.btnCheckSync);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckClick();
            }
        });
        btnCancelTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelTestClick();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });
        btnCheckSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckSyncClick();
            }
        });
    }

    public void onCheckClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        PrinceOfVersionsCall call = updater.checkForUpdates(loader, defaultCallback);
        replaceCall(call);
    }

    public void onCheckSyncClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Result result = updater.checkForUpdates(loader);
                    toastItOnMainThread("Update check finished with status " + result.getStatus() + " and version " + result.getVersion(),
                            Toast.LENGTH_LONG);
                } catch (Throwable throwable) {
                    toastItOnMainThread("Error occurred " + throwable.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }, "Example thread");
        thread.start();
    }

    public void onCancelTestClick() {
        /*  same call as few lines higher, but using another loader, this one is very slow loader  */
        PrinceOfVersionsCall call = updater.checkForUpdates(slowLoader, defaultCallback);
        replaceCall(call);
    }

    public void onCancelClick() {
        /*  cancel current checking request, checking if context is not consumed yet is not necessary   */
        if (call != null) {
            call.cancel();
        }
    }

    private void replaceCall(PrinceOfVersionsCall call) {
        /*  started new checking, kill current one if not dead and remember new context */
        if (this.call != null) {
            this.call.cancel();
        }
        this.call = call;
    }

    protected void toastIt(final String message, final int duration) {
        Toast.makeText(getApplicationContext(), message, duration).show();
    }

    protected void toastItOnMainThread(final String message, final int duration) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toastIt(message, duration);
            }
        });
    }

    private Loader createSlowLoader(final Loader loader) {
        return new Loader() {
            @Override
            public String load() throws Throwable {
                Thread.sleep(2000);
                return loader.load();
            }
        };
    }
}
