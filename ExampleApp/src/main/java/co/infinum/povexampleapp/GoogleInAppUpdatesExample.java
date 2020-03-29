package co.infinum.povexampleapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import co.infinum.princeofversions.Loader;
import co.infinum.princeofversions.NetworkLoader;
import co.infinum.princeofversions.PrinceOfVersionsCancelable;
import co.infinum.princeofversions.UpdateInfo;
import co.infinum.princeofversions.UpdateResult;
import co.infinum.princeofversions.UpdateStatus;
import co.infinum.queenofversions.QueenOfVersions;
import co.infinum.queenofversions.QueenOfVersionsInAppUpdateInfo;
import java.util.Map;
import javax.annotation.Nonnull;

public class GoogleInAppUpdatesExample extends AppCompatActivity implements QueenOfVersions.Callback {

    private static final String TAG = "GoogleInAppUpdates";

    private QueenOfVersions queenOfVersions;

    private Loader loader;

    private PrinceOfVersionsCancelable cancelable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_inapp_updates);

        initUI();

        queenOfVersions = new QueenOfVersions.Builder()
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
        cancelable = queenOfVersions.checkForUpdates(loader, this);
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
    public void onDownloading(@Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdate, long bytesDownloadedSoFar, long totalBytesToDownload) {
        Toast.makeText(
                this,
                String.format("Downloading update %s...", 100.0 * bytesDownloadedSoFar / totalBytesToDownload),
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * This method is called when Google update finished with downloading and it have started with installment.
     */
    @Override
    public void onInstalling(@Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
        Toast.makeText(this, String.format("Installing update %s...", inAppUpdateInfo.versionCode()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMandatoryUpdateNotAvailable(
            int mandatoryVersion,
            @Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            @Nonnull Map<String, String> metadata,
            @Nonnull UpdateInfo updateInfo
    ) {
        Toast.makeText(this, "Mandatory update is not available on Google!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloaded(@NonNull QueenOfVersions.UpdateHandler handler, @NonNull QueenOfVersionsInAppUpdateInfo inAppUpdate) {
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
    public void onInstalled(@Nonnull QueenOfVersionsInAppUpdateInfo appUpdateInfo) {
        Toast.makeText(this, "Installed update!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called if the update is by whatever reason put on hold and it has to wait before it can be processed.
     */
    @Override
    public void onPending(@Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdateInfo) {
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

    @Override
    public void onUpdateAccepted(
            @Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            @Nonnull UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    ) {
        Toast.makeText(this, "Update accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateDeclined(
            @Nonnull QueenOfVersionsInAppUpdateInfo inAppUpdateInfo,
            @Nonnull UpdateStatus updateStatus,
            @Nullable UpdateResult updateResult
    ) {
        Toast.makeText(this, "Update declined", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cancelable != null) {
            cancelable.cancel();
        }
    }
}
