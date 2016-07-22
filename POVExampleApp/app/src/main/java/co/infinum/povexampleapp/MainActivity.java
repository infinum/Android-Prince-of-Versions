package co.infinum.povexampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import co.infinum.princeofversions.DefaultUpdater;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.LoaderValidationException;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.interfaces.UpdateChecker;
import co.infinum.princeofversions.network.NetworkLoaderFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "exampleappActivityMain";
    private UpdateChecker updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = (Button)findViewById(R.id.btnCheck);

        updater = new DefaultUpdater(MainActivity.this, new UpdaterCallback() {
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
        });
        final LoaderFactory loaderFactory = new NetworkLoaderFactory("http://pastebin.com/raw/41N8stUD");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updater.checkForUpdates(loaderFactory);
            }
        });

        findViewById(R.id.btnCancelTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updater.checkForUpdates(new LoaderFactory() {
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
                });
            }
        });
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updater.cancel();
            }
        });
    }

    private void toastIt(final String message, final int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, duration).show();
            }
        });
    }
}
