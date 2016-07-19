package co.infinum.povexampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.infinum.princeofversions.DefaultUpdater;
import co.infinum.princeofversions.callbacks.UpdaterCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DefaultUpdater object = new DefaultUpdater(this);
        object.checkForUpdates("", new UpdaterCallback() {
            @Override
            public void onNewUpdate(String version, boolean isMandatory) {

            }

            @Override
            public void onNoUpdate() {

            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
