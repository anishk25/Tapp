package app.anish.com.tapp.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import app.anish.com.tapp.R;

/**
 * Dialog for logging into Linkedin. This will be used when the user
 * doesn't have the LinkedIn on their Phone.
 * Reference : https://github.com/mario595/android-oauth-linkedin-example/blob/master/src/co/uk/manifesto/linkedinauthexample/MainActivity.java
 * Created by anish_khattar25 on 7/1/17.
 */

public class LinkedinLoginDialog extends Dialog {


    private static final String LINKEDIN_API_KEY = "86900jvdihlkxb";
    private static final String LINKEDIN_SECRET_KEY = "oqHFGLA1FYrqTmAT";

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


    private ProgressDialog mProgressDialog;
    private WebView mWebView;

    public LinkedinLoginDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
    }



    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.wvWebView);
        mWebView.requestFocus(View.FOCUS_DOWN);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView webView, String url) {
                // method is executed each time a page is finished loading
                if (mProgressDialog != null && mProgressDialog.isShowing()) {

                }
            }
        });
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
}
