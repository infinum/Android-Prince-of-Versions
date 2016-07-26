package co.infinum.povexampleapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.infinum.princeofversions.BaseLoader;
import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.LoaderValidationException;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdateConfigLoader;

public class NetworkWithLoginExample extends BaseExampleActivity {

    public static final String TAG = "POV_NETWORK_WITH_LOGIN";

    @Bind(R.id.pastebin_username)
    protected EditText usernameView;

    @Bind(R.id.pastebin_password)
    protected EditText passwordView;

    @Bind(R.id.pastebin_url)
    protected EditText urlView;

    private PrinceOfVersions updater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_with_login);
        ButterKnife.bind(this);

        /*  create new instance of updater associated with application context  */
        updater = new PrinceOfVersions(this);
    }

    @OnClick(R.id.btn_pastebin)
    public void onLoginAndCheckClick() {
        /*  use updater with custom loader factory to check for updates from private pastebin file  */
        updater.checkForUpdates(loaderFactory, defaultCallback);
    }

    /*  Create new specific factory for creating loader */
    private LoaderFactory loaderFactory = new LoaderFactory() {

        @Override
        public UpdateConfigLoader newInstance() {
            return createLoader(
                    "http://pastebin.com/login.php",
                    urlView.getText().toString(),
                    usernameView.getText().toString(),
                    passwordView.getText().toString()
            );
        }

    };

    /**
     * Creates custom loader. Loader loads update configuration from private pastebin file using provided username and password to log in
     * into Pastebin account where private file is stored.
     * @param loginUrl URL for getting and posting to login page.
     * @param contentUrl URL for fetching content.
     * @param username Account username.
     * @param password Account password.
     * @return New instance of loader for loading from private Pastebin file.
     */
    private UpdateConfigLoader createLoader(final String loginUrl, final String contentUrl, final String username, final String password) {
        return new BaseLoader() {

            private List<String> cookies = new ArrayList<>();

            private void login(String csrf) throws IOException {
                URL url = new URL(loginUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    for (String cookie : cookies) {
                        conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                    }
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    String params = createLoginData(csrf);
                    dos.writeBytes(params);
                    dos.flush();
                    dos.close();
                    Log.d(TAG, "Sending login request...");
                    Log.d(TAG, "with parameters: " + params);
                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response code: " + responseCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e);
                } finally {
                    conn.disconnect();
                }
            }

            private String createLoginData(String csrf) {
                try {
                    return "csrf_token_login=" + URLEncoder.encode(csrf, "UTF-8")
                            + "&submit_hidden=submit_hidden"
                            + "&submit=Login"
                            + "&user_name=" + URLEncoder.encode(username, "UTF-8")
                            + "&user_password=" + URLEncoder.encode(password, "UTF-8");
                } catch (UnsupportedEncodingException impossible) {
                    return "";
                }
            }


            private String getPage(String pageUrl) throws IOException {
                URL url = new URL(pageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setUseCaches(false);
                    for (String cookie : cookies) {
                        conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                    }
                    Log.d(TAG, "Sending get request to url: " + pageUrl);
                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response code: " + responseCode);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream(),
                                    "UTF-8"
                            )
                    );
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    cookies = conn.getHeaderFields().get("Set-Cookie");
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e);
                } finally {
                    conn.disconnect();
                }
            }

            @Override
            public String load() throws IOException, InterruptedException {
                CookieHandler.setDefault(new CookieManager());
                String loginPage = getPage(loginUrl);
                ifTaskIsCancelledThrowInterrupt();
                Document document = Jsoup.parse(loginPage);
                document.charset(Charset.forName("UTF-8"));
                Elements selector = document.select("input[name=csrf_token_login]");
                String csrf = "";
                if (selector.size() > 0) {
                    csrf = selector.get(0).val();
                }
                Log.d(TAG, "CSRF: " + csrf);
                ifTaskIsCancelledThrowInterrupt();
                login(csrf);
                ifTaskIsCancelledThrowInterrupt();
                String content = getPage(contentUrl);
                ifTaskIsCancelledThrowInterrupt(); // if cancelled here no need to read stream at all
                return content;
            }

            @Override
            public void validate() throws LoaderValidationException {
                if (username == null || password == null) {
                    throw new LoaderValidationException("Credentials not set.");
                }
                if (contentUrl == null || loginUrl == null) {
                    throw new LoaderValidationException("Url not set.");
                }
            }
        };
    }

}
