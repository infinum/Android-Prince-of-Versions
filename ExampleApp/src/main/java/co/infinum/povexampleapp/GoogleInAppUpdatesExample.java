package co.infinum.povexampleapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.NetworkLoader;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.queenofversions.QueenOfVersions;
import java.util.Map;

public class GoogleInAppUpdatesExample extends AppCompatActivity implements QueenOfVersions.Callback {

    private static final String TAG = "GoogleInAppUpdates";

    private final int REQUEST_CODE = 420;

    private PrinceOfVersions princeOfVersions;

    private QueenOfVersions queenOfVersions;

    private Loader loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_inapp_updates);

        initUI();

        princeOfVersions = new PrinceOfVersions.Builder().build(this);
        queenOfVersions = new QueenOfVersions.Builder()
                .withCallback(this)
                .withRequestCode(REQUEST_CODE)
                .build(this);
        loader = new NetworkLoader("http://pastebin.com/raw/QFGjJrLP");
    }

    private void initUI() {
        Button checkUpdatesButton = findViewById(R.id.checkUpdatesButton);
        checkUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckUpdatesClick();
            }
        });
    }

    private void onCheckUpdatesClick() {
        princeOfVersions.checkForUpdates(loader, queenOfVersions.getPrinceOfVersionsCallback());
    }

    /**
     * This method is called if there is no Google update. If there is no Prince update, the Google update could still be called if there
     * is a new update on Google Play store.
     */
    @Override
    public void onNoUpdate(Map<String, String> metadata, UpdateInfo updateInfo) {
        Toast.makeText(this, "No updates!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when Google Update is downloading.
     */
    @Override
    public void onDownloading() {
        Toast.makeText(this, "Downloading update...", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when Google update finished with downloading and it have started with installment.
     */
    @Override
    public void onInstalling() {
        Toast.makeText(this, "Installing update...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMandatoryUpdateNotAvailable(
            int requiredVersion,
            int availableVersion,
            Map<String, String> metadata,
            UpdateInfo updateInfo
    ) {
        Toast.makeText(this, "Mandatory update is not available on Google!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloaded(QueenOfVersions.UpdateHandler handler) {
        Toast.makeText(this, "Downloaded!", Toast.LENGTH_SHORT).show();
        handler.completeUpdate();
    }

    /**
     * This method is called if by whatever reason the update is cancelled.
     */
    @Override
    public void onCanceled() {
        Toast.makeText(this, "Canceled update!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called after the update is installed. Both Flexible and Immediate restart the app after installation, so
     * this method is rarely useful.
     */
    @Override
    public void onInstalled() {
        Toast.makeText(this, "Installed update!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called if the update is by whatever reason put on hold and it has to wait before it can be processed.
     */
    @Override
    public void onPending() {
        Toast.makeText(this, "Update pending...", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called if something went wrong during an update.
     *
     * @param throwable instance of exception that caused update to fail
     */
    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, "Failed updated! Check log!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Exception:", throwable.fillInStackTrace());
    }
}
