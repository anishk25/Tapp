package app.anish.com.tapp.dialogs;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import app.anish.com.tapp.BuildConfig;
import app.anish.com.tapp.R;
import app.anish.com.tapp.linkedin_auth.LinkedInWebCredRetrieverService;
import okhttp3.HttpUrl;


/**
 * Dialog for logging into Linkedin. This will be used when the user
 * doesn't have the LinkedIn on their Phone.
 * Created by anish_khattar25 on 7/1/17.
 */

public class LinkedInWebViewLoginDialog extends Dialog {

    private static final String LOG_TAG = LinkedInWebViewLoginDialog.class.getCanonicalName();

    // Used to prevent CSRF attacks
    private static final String STATE = "1i4F65[*39ml77x";

    // Url that LinkedIn Auth Process will redirect to
    public static final String REDIRECT_URI = "http://app.tapp.anish.linkedin.redirecturl";

    // Constants to build URLS
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String RESPONSE_TYPE = "code";
    private static final String AUTHORIZATION_CODE_PARAM = RESPONSE_TYPE;
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    private ProgressDialog mProgressDialog;
    private WebView mWebView;

    public LinkedInWebViewLoginDialog(Context context) {
        super(context);
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
        mWebView.setWebViewClient(new LinkedInWebViewClient());
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
                .addQueryParameter(CLIENT_ID_PARAM, BuildConfig.LINKEDIN_API_KEY)
                .addQueryParameter(STATE_PARAM, STATE)
                .addQueryParameter(REDIRECT_URI_PARAM, REDIRECT_URI)
                .build()
                .toString();
    }

    private class LinkedInWebViewClient extends WebViewClient {
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
                    Toast.makeText(getContext(), "State token doesn't match. Possible CSRF Attack", Toast.LENGTH_LONG).show();
                    dismiss();
                    return true;
                }

                // If user doesn't allow authorization to our application,
                // the authorizationToken will be null
                String authorizationToken = uri.getQueryParameter(AUTHORIZATION_CODE_PARAM);
                if (authorizationToken == null) {
                    Toast.makeText(getContext(), "The user didn't allow authroization", Toast.LENGTH_LONG).show();
                    dismiss();
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
            Intent intent = new Intent(getContext(), LinkedInWebCredRetrieverService.class);
            intent.putExtra(LinkedInWebCredRetrieverService.LINKEDIN_AUTHORIZATION_TOKEN_KEY, authorizationToken);
            getContext().startService(intent);
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.dismiss();
    }
}
