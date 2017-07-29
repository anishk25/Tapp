package app.anish.com.tapp.dialogs;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkedin.platform.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import app.anish.com.tapp.R;
import app.anish.com.tapp.utils.HttpUtils;


/**
 * Dialog for logging into Linkedin. This will be used when the user
 * doesn't have the LinkedIn on their Phone.
 * Created by anish_khattar25 on 7/1/17.
 */

public class LinkedInWebViewLoginDialog extends Dialog {

    private static final String LOG_TAG = LinkedInWebViewLoginDialog.class.getCanonicalName();
    public static final String LINKEDIN_API_KEY = "86900jvdihlkxb";
    public static final String LINKEDIN_SECRET_KEY = "oqHFGLA1FYrqTmAT";

    // Used to prevent CSRF attacks
    private static final String STATE = "1i4F65[*39ml77x";

    // Url that Linkedin Auth Process will redirect to
    private static final String REDIRECT_URI = "http://app.tapp.anish.linkedin.redirecturl";

    // Constants to build URLS
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE ="code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";


    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private static final String TOKEN_VALUE_JSON_KEY = "access_token";
    private static final String TOKEN_EXPIRE_JSON_KEY = "expires_in";


    private ProgressDialog mProgressDialog;
    private AuthListener authListener;
    private WebView mWebView;

    public LinkedInWebViewLoginDialog(Context context, AuthListener authListener) {
        super(context);
        this.authListener = authListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        initWebView();
        loadAuthUrl();
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.wvWebView);
        mWebView.requestFocus(View.FOCUS_DOWN);
        mWebView.setWebViewClient(new LinkedinWebViewClient());
    }

    private void loadAuthUrl() {
        String authUrl = getAuthorizationUrl();
        mProgressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
        mWebView.loadUrl(authUrl);
    }


    /**
     * Method that generates the url for get the access token from the Service
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        return TextUtils.join("",
                new String [] {
                        ACCESS_TOKEN_URL,QUESTION_MARK,GRANT_TYPE_PARAM,
                        EQUALS, GRANT_TYPE, AMPERSAND, RESPONSE_TYPE_VALUE,
                        EQUALS, authorizationToken, AMPERSAND, CLIENT_ID_PARAM,
                        EQUALS, LINKEDIN_API_KEY, AMPERSAND, REDIRECT_URI_PARAM,
                        EQUALS, REDIRECT_URI, AMPERSAND, SECRET_KEY_PARAM,EQUALS,
                        LINKEDIN_SECRET_KEY
                }
                );
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */
    private static String getAuthorizationUrl() {
        return TextUtils.join("",
                    new String [] {
                            AUTHORIZATION_URL,QUESTION_MARK,RESPONSE_TYPE_PARAM,EQUALS,
                            RESPONSE_TYPE_VALUE,AMPERSAND,CLIENT_ID_PARAM,EQUALS,LINKEDIN_API_KEY,
                            AMPERSAND,STATE_PARAM,EQUALS,STATE, AMPERSAND, REDIRECT_URI_PARAM,
                            EQUALS,REDIRECT_URI
                    }
                );
    }

    private void dismissDialogInError(String errorMsg) {
        Log.e(LOG_TAG, errorMsg);
        dismiss();
        authListener.onAuthError(errorMsg);
    }

    private void dismissDialogInSuccess(AccessToken accessToken) {
        dismiss();
        authListener.onAuthSuccess(accessToken);
    }


    private class LinkedinWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String authorizationUrl) {
            Uri uri = Uri.parse(authorizationUrl);
            if (uri.toString().startsWith(REDIRECT_URI)) {
                String stateToken = uri.getQueryParameter(STATE_PARAM);
                if (stateToken == null || !stateToken.equals(STATE)) {
                    String msg = "State token doesn't match. Possible CSRF Attach";
                    dismissDialogInError(msg);
                    return true;
                }

                // If user doesn't allow authorization to our application,
                // the authorizationToken will be null
                String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                if (authorizationToken == null) {
                    dismissDialogInError("The user didn't allow authorization");
                    return true;
                }

                Log.i(LOG_TAG, "Auth token received " + authorizationToken);
                String accessTokenUrl = getAccessTokenUrl(authorizationToken);

                // Make the request in an Async Task
                new PostRequestAsyncTask().execute(accessTokenUrl);
            } else {
                // default behaviour
                String url = uri.toString();
                Log.i(LOG_TAG, "Webview redirecting to : " + uri);
                mWebView.loadUrl(url);
            }

            return true;
        }
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Throwable> {

        private volatile AccessToken accessToken;

        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null || !mProgressDialog.isShowing()) {
                mProgressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading), true);
            }
        }

        @Override
        protected void onPostExecute(Throwable throwable) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (throwable != null) {
                dismissDialogInError(throwable.getMessage());
            } else {
                dismissDialogInSuccess(accessToken);
            }
        }

        @Override
        protected Throwable doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                try {
                    accessToken = getLinkedinToken(url);
                    Log.d("LINKEDIN TOKEN", "LinkedIn Token is " + accessToken.toString());
                    return null;
                } catch (IOException e) {
                    return new Exception("Error Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    return new Exception("Error Parsing Http response " + e.getLocalizedMessage());
                }
            }

            return new Exception("No urls provided to task");
        }

        /**
         * Extracts token from URL and saves it to Shared Preferences
         * @param url
         */
        private AccessToken getLinkedinToken (String url) throws IOException, JSONException{
            JSONObject webJSON = getLinkedInJSONToken(url);
            JSONObject sessionJSON = getSessionJSON(webJSON);
            return AccessToken.buildAccessToken(sessionJSON);
        }

        /**
         * Retrieves the LinkedIn Token stored in the URL
         * @param url
         * @return {@link JSONObject} containing the token information
         * @throws IOException
         * @throws JSONException
         */
        private JSONObject getLinkedInJSONToken (String url) throws IOException, JSONException {
            URL urlObj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestMethod("POST");

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 200) {
                String response = HttpUtils.getResponseString(urlConnection);
                return new JSONObject(response);
            }

            return null;
        }

        /**
         * converts the JSON object received from the web
         * to a JSON object to be used to build {@link com.linkedin.platform.AccessToken}
         * @param webJson
         * @return {@link JSONObject}
         */
        private JSONObject getSessionJSON(JSONObject webJson) throws JSONException {
            JSONObject jsonObject = new JSONObject();

            //Calculate date of expiration, the expiration value
            //return by web authorization tells us in how many
            // seconds the token will expire from the time it was
            // retrieved
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, (Integer) webJson.get(TOKEN_EXPIRE_JSON_KEY));
            String expireDateInMs = Long.toString(calendar.getTimeInMillis());

            jsonObject.put(AccessToken.ACCESS_TOKEN_VALUE, webJson.get(TOKEN_VALUE_JSON_KEY));
            jsonObject.put(AccessToken.EXPIRES_ON, expireDateInMs);
            return jsonObject;
        }
    }


    public interface AuthListener {
        void onAuthSuccess(AccessToken accessToken);;
        void onAuthError(String error);
    }
}
