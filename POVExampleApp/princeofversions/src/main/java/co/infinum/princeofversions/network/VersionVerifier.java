package co.infinum.princeofversions.network;

import com.github.zafarkhaja.semver.Version;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.infinum.princeofversions.helpers.ContextHelper;

/**
 * Created by stefano on 13/07/16.
 */
public class VersionVerifier extends AsyncTask<Void, Void, Void> {

    public static final String ANDROID = "android";

    public static final String MINIMUM__VERSION = "minimum__version";

    public static final String OPTIONAL_UPDATE = "optional_update";

    public static final String VERSION = "version";

    private static final String TAG = "AsyncTask";

    private Context context;

    public VersionVerifier() {
        this.context = ContextHelper.getContext();
    }

    public void getVersion() {

        HttpURLConnection urlConnection;

        String url = "http://pastebin.com/raw/41N8stUD";
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setChunkedStreamingMode(0);

            InputStream response = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(response));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line + "\n");
            }

            Log.e(TAG, out.toString());

            JSONObject apiResponse = new JSONObject(out.toString());

            checkVersions(apiResponse);

        } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkVersions(JSONObject apiResponse) throws JSONException, PackageManager.NameNotFoundException {

        String min = apiResponse.getJSONObject(ANDROID).getString(MINIMUM__VERSION);

        String update = apiResponse.getJSONObject(ANDROID).getJSONObject(OPTIONAL_UPDATE).getString(VERSION);
        String current = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

        Version minVersion = Version.valueOf(min);
        Version updateVersion = Version.valueOf(update);
        Version currentVersion = Version.valueOf(current);

        //TODO here needs to be handled the use-case where current is "1.0", the lib requires for it to be "1.0.0" at minimum, so
        //TODO if its "1.0" it just plainly crashes

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getVersion();
        return null;
    }
}
