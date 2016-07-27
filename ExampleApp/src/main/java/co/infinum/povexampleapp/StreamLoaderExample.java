package co.infinum.povexampleapp;

import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.infinum.princeofversions.CheckForUpdatesCallingContext;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;
import co.infinum.princeofversions.exceptions.LoaderValidationException;
import co.infinum.princeofversions.loaders.StreamLoader;

public class StreamLoaderExample extends BaseExampleActivity {

    public static final String TAG = "POV_STREAM_USAGE";

    private PrinceOfVersions updater;
    private LoaderFactory loaderFactory;
    private CheckForUpdatesCallingContext povContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_usage);
        ButterKnife.bind(this);

        /*  create new instance of updater associated with application context   */
        updater = new PrinceOfVersions(this);
        /*  create specific loader factory for loading from stream    */
        loaderFactory = new LoaderFactory() {
            @Override
            public UpdateConfigLoader newInstance() {
                // create new stream for every read
                return new StreamLoader(getResources().openRawResource(R.raw.update));
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        onCancelClick();
    }

    @OnClick(R.id.btnCheck)
    public void onCheckClick() {
        /*  call check for updates for start checking and remember return value if you need cancel option    */
        CheckForUpdatesCallingContext context = updater.checkForUpdates(loaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    @OnClick(R.id.btnCancelTest)
    public void onCancelTestClick() {
        /*  same call as few lines higher, but using another loader, this one is very slow loader just to demonstrate cancel
        functionality. */
        CheckForUpdatesCallingContext context = updater.checkForUpdates(slowLoaderFactory, defaultCallback);
        replacePOVContext(context);
    }

    @OnClick(R.id.btnCancel)
    public void onCancelClick() {
        /*  cancel current checking request, checking if context is not consumed yet is not necessary   */
        if (povContext != null && !povContext.isConsumed()) {
            povContext.cancel();
        }
    }

    private void replacePOVContext(CheckForUpdatesCallingContext povContext) {
        /*  started new checking, kill current one if not dead and remember new context */
        if (this.povContext != null && !this.povContext.isConsumed() && !this.povContext.isCancelled()) {
            toastIt(getString(R.string.replace), Toast.LENGTH_SHORT);
            this.povContext.cancel();
        }
        this.povContext = povContext;
    }

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

}
