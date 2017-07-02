package app.anish.com.tapp.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import app.anish.com.tapp.R;

/**
 * Dialog for logging into Linkedin. This will be used when the user
 * doesn't have the LinkedIn on their Phone.
 * Reference : https://stackoverflow.com/questions/33820783/stuck-in-linkedin-login-via-webview
 * Created by anish_khattar25 on 7/1/17.
 */

public class LinkedinLoginDialog extends Dialog {

    private ProgressDialog mProgressDialog;
    private WebView mWebView;
    private Context mContext;


    public LinkedinLoginDialog(Context context, ProgressDialog progressDialog) {
        super(context);
        this.mContext = context;
        this.mProgressDialog = progressDialog;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
    }

    private void initWebView() {

    }
}
