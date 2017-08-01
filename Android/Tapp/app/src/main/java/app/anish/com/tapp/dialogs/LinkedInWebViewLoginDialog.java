package app.anish.com.tapp.dialogs;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkedin.platform.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import app.anish.com.tapp.R;
import app.anish.com.tapp.retrofit.LinkedInAuthEndpoint;
import app.anish.com.tapp.retrofit.LinkedInWebToken;
import okhttp3.HttpUrl;

import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    private static final String AUTHORIZATION_CODE_PARAM = RESPONSE_TYPE;
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";


    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ACCESS_TOKEN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final LinkedInAuthEndpoint authEndpoint = retrofit.create(LinkedInAuthEndpoint.class);

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
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */
    private static String getAuthorizationUrl() {

        return HttpUrl
                .parse(AUTHORIZATION_URL)
                .newBuilder()
                .addQueryParameter(RESPONSE_TYPE_PARAM, RESPONSE_TYPE)
                .addQueryParameter(CLIENT_ID_PARAM, LINKEDIN_API_KEY)
                .addQueryParameter(STATE_PARAM, STATE)
                .addQueryParameter(REDIRECT_URI_PARAM, REDIRECT_URI)
                .build()
                .toString();
    }

    private void dismissDialogInError(final String errorMsg) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Log.e(LOG_TAG, errorMsg);
//                dismiss();
//                authListener.onAuthError(errorMsg);
//            }
//        });

        Log.e(LOG_TAG, errorMsg);
        dismiss();
        authListener.onAuthError(errorMsg);
    }

    private void dismissDialogInSuccess(final AccessToken accessToken) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                dismiss();
//                authListener.onAuthSuccess(accessToken);
//            }
//        });
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
                String authorizationToken = uri.getQueryParameter(AUTHORIZATION_CODE_PARAM);
                if (authorizationToken == null) {
                    dismissDialogInError("The user didn't allow authorization");
                    return true;
                }

                Log.i(LOG_TAG, "Auth token received " + authorizationToken);
                makeTokenRequest(authorizationToken);
            } else {
                // default behaviour
                String url = uri.toString();
                Log.i(LOG_TAG, "Webview redirecting to : " + uri);
                mWebView.loadUrl(url);
            }

            return true;
        }

        private void makeTokenRequest(String authorizationToken) {

            Call<LinkedInWebToken> call = authEndpoint.getToken(GRANT_TYPE, authorizationToken, REDIRECT_URI, LINKEDIN_API_KEY, LINKEDIN_SECRET_KEY);
            call.enqueue(new Callback<LinkedInWebToken>() {
                @Override
                public void onResponse(Call<LinkedInWebToken> call, Response<LinkedInWebToken> response) {
                    if (response.isSuccessful()) {
                        processResponseSuccess(response);
                    } else {
                        processResponseFailure(response);
                    }
                }

                @Override
                public void onFailure(Call<LinkedInWebToken> call, Throwable t) {
                    dismissDialogInError(t.getMessage());
                }
            });
        }

        private void processResponseSuccess(Response<LinkedInWebToken> response) {
            try {
                dismissDialogInSuccess(getAccessToken(response.body()));
            } catch (JSONException e) {
                dismissDialogInError(e.getMessage());
            }
        }

        private void processResponseFailure(Response<LinkedInWebToken> response) {
            String errorMsg = "Error making token request";
            try {
                errorMsg += " Error: " + response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dismissDialogInError(errorMsg);
        }

        /**
         * converts the webToken parameter to {@link AccessToken}
         * @param webToken
         * @return {@link JSONObject}
         */
        private AccessToken getAccessToken (LinkedInWebToken webToken) throws JSONException {
            JSONObject jsonObject = new JSONObject();

            //Calculate date of expiration, the expiration value
            //return by web authorization tells us in how many
            // seconds the token will expire from the time it was
            // retrieved
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, webToken.getExpirationInSec());
            String expireDateInMs = Long.toString(calendar.getTimeInMillis());

            jsonObject.put(AccessToken.ACCESS_TOKEN_VALUE, webToken.getTokenValue());
            jsonObject.put(AccessToken.EXPIRES_ON, expireDateInMs);
            return AccessToken.buildAccessToken(jsonObject);
        }
    }

    public interface AuthListener {
        void onAuthSuccess(AccessToken accessToken);;
        void onAuthError(String error);
    }
}
