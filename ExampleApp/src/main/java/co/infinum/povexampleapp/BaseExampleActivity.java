package co.infinum.povexampleapp;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import co.infinum.princeofversions.callbacks.UpdaterCallback;
import co.infinum.princeofversions.common.ErrorCode;

public class BaseExampleActivity extends AppCompatActivity {

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

    protected void toastIt(final String message, final int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, duration).show();
            }
        });
    }
}
