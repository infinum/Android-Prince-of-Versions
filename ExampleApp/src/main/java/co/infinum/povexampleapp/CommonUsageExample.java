package co.infinum.povexampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import co.infinum.princeofversions.CheckForUpdates;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.loaders.factories.NetworkLoaderFactory;

public class CommonUsageExample extends AppCompatActivity {

    protected UpdaterCallback defaultCallback = new UpdaterCallback() {
        @Override
        public void onNewUpdate(String version, boolean isMandatory) {
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
            toastIt(getString(R.string.no_update_available), Toast.LENGTH_SHORT);
        }

        @Override
        public void onError(@ErrorCode int error) {
            toastIt(String.format(getString(R.string.update_error), error), Toast.LENGTH_SHORT);
        }
    };

    private PrinceOfVersions updater;

    private LoaderFactory loaderFactory;

    private CheckForUpdates povContext;

    /**
     * This factory creates a very slow loader, just to give you enough time to invoke cancel option.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_usage);

        initUI();

        /*  create new instance of updater associated with application context   */
        updater = new PrinceOfVersions(this);
        /*  create specific loader factory for loading from internet    */
        loaderFactory = new NetworkLoaderFactory("http://pastebin.com/raw/8yJBxJsg");
    }

    @Override
    protected void onStop() {
        super.onStop();
        onCancelClick();
    }

    private void initUI() {
        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        Button btnCancelTest = (Button) findViewById(R.id.btnCancelTest);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
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
    }

    public void onCheckClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        CheckForUpdates context = updater.checkForUpdates(loaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    public void onCancelTestClick() {
        /*  same call as few lines higher, but using another loader, this one is very slow loader just to demonstrate cancel
        functionality. */
        CheckForUpdates context = updater.checkForUpdates(slowLoaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    public void onCancelClick() {
        /*  cancel current checking request, checking if context is not consumed yet is not necessary   */
        if (povContext != null && !povContext.isConsumed()) {
            povContext.cancel();
        }
    }

    private void replacePOVContext(CheckForUpdates povContext) {
        /*  started new checking, kill current one if not dead and remember new context */
        if (this.povContext != null && !this.povContext.isConsumed() && !this.povContext.isCancelled()) {
            toastIt(getString(R.string.replace), Toast.LENGTH_SHORT);
            this.povContext.cancel();
        }
        this.povContext = povContext;
    }

    protected void toastIt(final String message, final int duration) {
        Toast.makeText(getApplicationContext(), message, duration).show();
    }

}
